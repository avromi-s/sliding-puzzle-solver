package com.avromi.slidingpuzzlesolver.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.avromi.slidingpuzzlesolver.R;
import com.avromi.slidingpuzzlesolver.classes.AppPreferenceManager;
import com.avromi.slidingpuzzlesolver.classes.AppPreferenceManager.MainActivityPreferences;
import com.avromi.slidingpuzzlesolver.models.classes.SlidingPuzzleNode;
import com.avromi.slidingpuzzlesolver.models.classes.SearchResult;
import com.avromi.slidingpuzzlesolver.classes.Utils;
import com.avromi.slidingpuzzlesolver.models.interfaces.SearchMethod;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;

import com.avromi.slidingpuzzlesolver.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // views:
    private GridLayout mPuzzleBoardGrid;
    private LinearLayout mControlsBar;
    private ExtendedFloatingActionButton mFab;
    private MaterialCardView[][] mCells;
    private Snackbar mSnackbar;

    // controls bar buttons:
    private Button mSkipToFirstButton;
    private Button mPreviousButton;
    private Button mPlayButton;
    private Button mNextButton;
    private Button mSkipToLastButton;

    // settings:
    private int mBoardSize;  // this is not final as the board size can be changed by the user in settings

    private SearchMethod<SlidingPuzzleNode> mSearchMethod;
    private CompletableFuture<SearchResult<SlidingPuzzleNode>> mSolutionSearchTask;

    // fields:
    private Utils mUtils;
    private AppPreferenceManager mAppPreferenceManager;
    private SearchResult<SlidingPuzzleNode> mSolution;
    private List<SlidingPuzzleNode> mSolutionPath;
    private int mSolutionViewingIndex = 0;
    private boolean mIsInEditMode = true;

//region Activity lifecycle methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.includeToolbar.toolbar);
        setupFields();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelSearchTaskIfRunning();
        saveSettingsToPreferences();
        saveStateToPreferences();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setSettingsFromPreferences();
        setStateFromPreferences();
        setupViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            cancelSearchTaskIfRunning();
            cancelPlayThroughRestOfSolution();
            refreshControlsBarButtonsEnabledStatus();
            enableEditingCells();
        } else if (id == R.id.action_clear) {
            cancelSearchTaskIfRunning();
            cancelPlayThroughRestOfSolution();
            resetSolutionViewing();
            clearScreenBoard();
            enableEditingCells();
            setAllControlsBarButtonsEnabledStatus(false);
            Toast.makeText(this, R.string.board_cleared, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            settingsLauncher.launch(intent);
        } else if (id == R.id.action_about) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.about)
                    .setMessage(R.string.about_message)
                    .setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_launcher))
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(getString(R.string.is_in_edit_mode_bundle_key),
                mIsInEditMode);
        outState.putSerializable(getString(R.string.numbers_entered_bundle_key),
                collectPiecesFromScreen());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mIsInEditMode = savedInstanceState.getBoolean(getString(R.string.is_in_edit_mode_bundle_key));
        refreshCellsEditStatus();

        int[][] boardPieces =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                        savedInstanceState.getSerializable(getString(R.string.numbers_entered_bundle_key), int[][].class) :
                        (int[][]) savedInstanceState.getSerializable(getString(R.string.numbers_entered_bundle_key));
        if (boardPieces != null)
            setScreenFromBoard(boardPieces);
    }

//endregion

//region Setup methods

    private void setupFields() {
        mUtils = new Utils(this);
        mAppPreferenceManager = AppPreferenceManager.getInstance(this);

        mPuzzleBoardGrid = binding.includeContentMain.includePuzzleBoard.puzzleBoardGrid;
        mControlsBar = binding.includeContentMain.includeControlsBar.controlsBar;
        mSkipToFirstButton = binding.includeContentMain.includeControlsBar.skipToFirstButton;
        mPreviousButton = binding.includeContentMain.includeControlsBar.previousButton;
        mPlayButton = binding.includeContentMain.includeControlsBar.playButton;
        mNextButton = binding.includeContentMain.includeControlsBar.nextButton;
        mSkipToLastButton = binding.includeContentMain.includeControlsBar.skipToLastButton;
        mFab = binding.includeFab.fab;
    }

    private void setupViews() {
        setupBoard();
        setupControlsBar();
        setupFab();
        setupSnackbar();

        boolean hasSolutionToLoad =
                mSolution != null && mSolution.getSolutionWasFound()
                        && mSolutionPath != null
                        && mSolutionPath.get(0).getBoardPieces().length == mBoardSize;
        if (hasSolutionToLoad) {
            disableEditingCells();
            setScreenFromBoard(mSolutionPath.get(mSolutionViewingIndex).getBoardPieces());
            setupSolutionViewing(mSolution, false);
            refreshControlsBarButtonsEnabledStatus();
        } else {
            enableEditingCells();
        }

    }

    private void setupBoard() {
        mCells = new MaterialCardView[mBoardSize][mBoardSize];
        mPuzzleBoardGrid.removeAllViews();
        mPuzzleBoardGrid.setColumnCount(mBoardSize);
        mPuzzleBoardGrid.setRowCount(mBoardSize);

        for (int row = 0; row < mBoardSize; row++) {
            for (int col = 0; col < mBoardSize; col++) {
                MaterialCardView cell = mUtils.getNewPuzzleBoardCell(row, col,
                        1, (mBoardSize * mBoardSize) - 1);
                mPuzzleBoardGrid.addView(cell);
                mCells[row][col] = cell;
            }
        }
    }

    private void setupControlsBar() {
        mSkipToFirstButton.setOnClickListener(view -> {
            showFirstStepInSolution();
            refreshControlsBarButtonsEnabledStatus();
        });

        mPreviousButton.setOnClickListener(view -> {
            showPreviousStepInSolution();
            refreshControlsBarButtonsEnabledStatus();
        });

        mPlayButton.setOnClickListener(view -> {
            disableEditingCells();
            playThroughRestOfSolution();
        });

        mNextButton.setOnClickListener(view -> {
            showNextStepInSolution();
            refreshControlsBarButtonsEnabledStatus();
        });

        mSkipToLastButton.setOnClickListener(view -> {
            showLastStepInSolution();
            refreshControlsBarButtonsEnabledStatus();
        });
    }

    private void setupFab() {
        mFab.setOnClickListener(view ->
        {
            disableEditingCells();
            mFab.setEnabled(false);
            try {
                SlidingPuzzleNode board = new SlidingPuzzleNode(collectPiecesFromScreen());
                mSolutionSearchTask = startNewSearchTask(board);
            } catch (IllegalArgumentException e) {
                enableEditingCells();
                mFab.setEnabled(true);
                mSnackbar.setText(R.string.error_finding_solution_check_input)
                        .setDuration(Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    private CompletableFuture<SearchResult<SlidingPuzzleNode>> startNewSearchTask(SlidingPuzzleNode boardToSearch) {
        // Start the search task and add completion and exception handling.
        // We return the original completable future (of the search task) so that the actual search operation
        // can be cancelled externally.

        CompletableFuture<SearchResult<SlidingPuzzleNode>> searchTask
                = CompletableFuture.supplyAsync(() -> mSearchMethod.search(boardToSearch));

        searchTask.thenAccept(result ->
                {
                    if (result.getSolutionWasFound()) {
                        runOnUiThread(() -> {
                            mSnackbar.setText(getString(R.string.found_solution) + " with " + (result.getSolutionPath().size() - 1) + " steps");
                            mSnackbar.setDuration(Snackbar.LENGTH_LONG);
                            mSnackbar.show();
                            setupSolutionViewing(result, true);
                            refreshControlsBarButtonsEnabledStatus();
                        });
                    } else {
                        runOnUiThread(() -> {
                            mSnackbar.setText(getString(R.string.couldnt_find_solution_try_again));
                            mSnackbar.setDuration(Snackbar.LENGTH_LONG);
                            mSnackbar.show();
                            enableEditingCells();
                        });
                    }
                    runOnUiThread(() -> mFab.setEnabled(true));
                })
                .exceptionally(result ->
                {
                    if (mSolutionSearchTask.isCancelled()) {
                        mSearchMethod.terminate();
                    } else {
                        runOnUiThread(() -> {
                            mSnackbar.setText(getString(R.string.error_finding_solution));
                            mSnackbar.setDuration(Snackbar.LENGTH_LONG);
                            mSnackbar.show();
                        });
                    }

                    runOnUiThread(() -> {
                        enableEditingCells();
                        mFab.setEnabled(true);
                    });

                    return null;
                });
        return searchTask;
    }

    private void setupSolutionViewing(SearchResult<SlidingPuzzleNode> solution, boolean resetViewingIndex) {
        mSolution = solution;
        mSolutionPath = mSolution.getSolutionPath();
        mSolutionViewingIndex = resetViewingIndex ? 0 : mSolutionViewingIndex;
    }

    private void resetSolutionViewing() {
        mSolution = null;
        mSolutionPath = null;
        mSolutionViewingIndex = 0;
    }

    private void setupSnackbar() {
        mSnackbar = Snackbar.make(binding.includeContentMain.getRoot(), "", Snackbar.LENGTH_LONG);
    }

//endregion

//region Update views

    private void refreshControlsBarButtonsEnabledStatus() {
        if (mSolutionPath == null || mSolutionPath.size() <= 1) {
            setAllControlsBarButtonsEnabledStatus(false);
        } else if (mSolutionViewingIndex == mSolutionPath.size() - 1) {
            mPreviousButton.setEnabled(true);
            mSkipToFirstButton.setEnabled(true);
            mNextButton.setEnabled(false);
            mSkipToLastButton.setEnabled(false);
            mPlayButton.setEnabled(false);
        } else if (mSolutionViewingIndex == 0) {
            mPreviousButton.setEnabled(false);
            mSkipToFirstButton.setEnabled(false);
            mNextButton.setEnabled(true);
            mSkipToLastButton.setEnabled(true);
            mPlayButton.setEnabled(true);
        } else if (mSolutionViewingIndex > 0 && mSolutionViewingIndex < mSolutionPath.size() - 1) {
            setAllControlsBarButtonsEnabledStatus(true);
        }
    }

    private void setAllControlsBarButtonsEnabledStatus(boolean val) {
        mPreviousButton.setEnabled(val);
        mSkipToFirstButton.setEnabled(val);
        mNextButton.setEnabled(val);
        mSkipToLastButton.setEnabled(val);
        mPlayButton.setEnabled(val);
    }

//endregion

//region Puzzle board

    private void cancelSearchTaskIfRunning() {
        if (mSolutionSearchTask != null && !mSolutionSearchTask.isDone()) {
            mSolutionSearchTask.cancel(true);
        }
    }

//region Cell management

    private int[][] collectPiecesFromScreen() {
        int[][] pieces = new int[mBoardSize][mBoardSize];

        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces.length; j++) {
                String input = ((EditText) mCells[i][j].getChildAt(0)).getText().toString();
                if (input.isEmpty()) {
                    pieces[i][j] = SlidingPuzzleNode.getEmptyPieceNumber(mBoardSize);
                } else {
                    pieces[i][j] = Integer.parseInt(input);
                }
            }
        }
        return pieces;
    }

    private void setScreenFromBoard(int[][] pieces) {
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces.length; j++) {
                EditText cellText = ((EditText) mCells[i][j].getChildAt(0));
                if (pieces[i][j] == SlidingPuzzleNode.getEmptyPieceNumber(pieces.length)) {
                    if (!mIsInEditMode) {
                        mCells[i][j].setVisibility(View.INVISIBLE);
                    }
                    cellText.setText("");
                } else {
                    mCells[i][j].setVisibility(View.VISIBLE);
                    cellText.setText(String.valueOf(pieces[i][j]));
                }
            }
        }
    }

    private void clearScreenBoard() {
        for (MaterialCardView[] mCell : mCells) {
            for (MaterialCardView materialCardView : mCell) {
                EditText cellText = ((EditText) materialCardView.getChildAt(0));
                cellText.setText("");
            }
        }
    }

    private void refreshCellsEditStatus() {
        if (mIsInEditMode) {
            enableEditingCells();
        } else {
            disableEditingCells();
        }
    }

    private void enableEditingCells() {
        mIsInEditMode = true;
        for (MaterialCardView[] row : mCells) {
            for (MaterialCardView cell : row) {
                cell.setVisibility(View.VISIBLE);

                EditText cellEditText = (EditText) cell.getChildAt(0);

                TypedValue typedValue = new TypedValue();
                getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true);
                cellEditText.setTextColor(typedValue.data);

                cellEditText.setEnabled(true);
                cellEditText.setBackgroundResource(com.google.android.material.R.drawable.abc_edit_text_material);  // add back underline
            }
        }
    }

    private void disableEditingCells() {
        mIsInEditMode = false;
        for (MaterialCardView[] row : mCells) {
            for (MaterialCardView cell : row) {
                EditText cellEditText = (EditText) cell.getChildAt(0);

                if (cellEditText.getText().toString().isEmpty()) {
                    cell.setVisibility(View.INVISIBLE);
                }

                TypedValue typedValue = new TypedValue();
                getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
                cellEditText.setTextColor(typedValue.data);

                cellEditText.setEnabled(false);
                cellEditText.setBackground(null);  // remove underline
            }
        }
    }

//endregion

//region Solution viewing

    private Handler mPlayThroughSolutionHandler;
    private void playThroughRestOfSolution() {
        long intervalBetweenMovesMs = 250L;
        mPlayThroughSolutionHandler = new Handler(Looper.getMainLooper());
        int timeOffset = 0;
        setAllControlsBarButtonsEnabledStatus(false);  // disable all while play through is going
        for (int i = mSolutionViewingIndex; i < mSolutionPath.size() - 1; i++) {
            mPlayThroughSolutionHandler.postDelayed(this::showNextStepInSolution, timeOffset * intervalBetweenMovesMs);
            timeOffset++;
        }
        mPlayThroughSolutionHandler.postDelayed(this::refreshControlsBarButtonsEnabledStatus, (timeOffset - 1) * intervalBetweenMovesMs);
    }

    private void cancelPlayThroughRestOfSolution() {
        if (mPlayThroughSolutionHandler != null) {
            mPlayThroughSolutionHandler.removeCallbacksAndMessages(null);
        }
    }

    private void showNextStepInSolution() {
        setScreenFromBoard(mSolutionPath.get(++mSolutionViewingIndex).getBoardPieces());
    }

    private void showPreviousStepInSolution() {
        setScreenFromBoard(mSolutionPath.get(--mSolutionViewingIndex).getBoardPieces());
    }

    private void showFirstStepInSolution() {
        mSolutionViewingIndex = 0;
        setScreenFromBoard(mSolutionPath.get(mSolutionViewingIndex).getBoardPieces());
    }

    private void showLastStepInSolution() {
        mSolutionViewingIndex = mSolutionPath.size() - 1;
        setScreenFromBoard(mSolutionPath.get(mSolutionViewingIndex).getBoardPieces());
    }
//endregion

//endregion

//region Preferences

    private final ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> setSettingsFromPreferences());

    public void setSettingsFromPreferences() {
        mAppPreferenceManager.getSettings().updateLocalValuesFromPreferences();
        mSearchMethod = mAppPreferenceManager.getSettings().getAlgorithmValue();
        mBoardSize = mAppPreferenceManager.getSettings().getBoardSizeValue();
    }

    public void saveSettingsToPreferences() {
        mAppPreferenceManager.getSettings().setAlgorithmValue(mSearchMethod);
        mAppPreferenceManager.getSettings().setBoardSizeValue(mBoardSize);
    }

    public void setStateFromPreferences() {
        MainActivityPreferences preferences = mAppPreferenceManager.getMainActivity();
        mSolutionViewingIndex = preferences.getBoardSolutionViewingIndex();
        mSolution = preferences.getBoardSolutionValue();

        if (mSolution != null) {
            mSolutionPath = mSolution.getSolutionPath();
            mIsInEditMode = false;
        } else {
            mSolutionPath = List.of();
            mIsInEditMode = true;
        }
    }

    public void saveStateToPreferences() {
        MainActivityPreferences preferences = mAppPreferenceManager.getMainActivity();
        preferences.setBoardSolutionValue(mSolution);
        preferences.setBoardSolutionViewingIndex(mSolutionViewingIndex);
        preferences.saveLocalValuesToPreferences();
    }

//endregion

}