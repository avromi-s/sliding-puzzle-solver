package com.avromi.slidingpuzzlesolver.classes;

import android.content.Context;
import android.content.SharedPreferences;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

import com.avromi.slidingpuzzlesolver.R;
import com.avromi.slidingpuzzlesolver.activities.MainActivity;
import com.avromi.slidingpuzzlesolver.models.classes.SearchResult;
import com.avromi.slidingpuzzlesolver.models.classes.SlidingPuzzleNode;
import com.avromi.slidingpuzzlesolver.models.searchmethods.AStar;
import com.avromi.slidingpuzzlesolver.models.searchmethods.BFS;
import com.avromi.slidingpuzzlesolver.models.searchmethods.BestFirst;
import com.avromi.slidingpuzzlesolver.models.searchmethods.DDFS;
import com.avromi.slidingpuzzlesolver.models.searchmethods.DFS;
import com.avromi.slidingpuzzlesolver.models.searchmethods.IDDFS;
import com.avromi.slidingpuzzlesolver.models.interfaces.SearchMethod;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Simplifies management of the app's different preferences.
 * */
public class AppPreferenceManager {
    private static AppPreferenceManager instance;
    private final Context appContext;
    private final Gson gson;
    private final SettingsPreferences settingsPreferences;
    private final MainActivityPreferences mainActivityPreferences;

    public static AppPreferenceManager getInstance(Context appContext) {
        if (instance == null) {
            instance = new AppPreferenceManager(appContext);
        }
        return instance;
    }

    private AppPreferenceManager(Context appContext) {
        this.appContext = appContext;
        this.gson = new Gson();
        this.settingsPreferences = new SettingsPreferences();
        this.mainActivityPreferences = new MainActivityPreferences();
    }

    public SettingsPreferences getSettings() {
        return this.settingsPreferences;
    }

    public MainActivityPreferences getMainActivity() {
        return this.mainActivityPreferences;
    }

    public class SettingsPreferences {
        private final SharedPreferences sharedPreferences;
        private final String algorithmKey;
        private SearchMethod<SlidingPuzzleNode> algorithmValue;
        private final String boardSizeKey;
        private int boardSizeValue;

        private SettingsPreferences() {
            this.sharedPreferences = getDefaultSharedPreferences(appContext);
            this.algorithmKey = appContext.getString(R.string.algorithm_setting_preference_key);
            this.boardSizeKey = appContext.getString(R.string.board_size_setting_key);
            updateLocalValuesFromPreferences();
        }

        public void updateLocalValuesFromPreferences() {
            String algorithmDefault = appContext.getResources().getString(R.string.algorithm_setting_default);
            String algorithm = this.sharedPreferences.getString(this.algorithmKey, algorithmDefault);

            switch (algorithm) {
                case "AStar":
                    setAlgorithmValue(new AStar());
                    break;
                case "BestFirst":
                    setAlgorithmValue(new BestFirst());
                    break;
                case "BFS":
                    setAlgorithmValue(new BFS());
                    break;
                case "DFS":
                    setAlgorithmValue(new DFS());
                    break;
                case "DDFS":
                    setAlgorithmValue(new DDFS());
                    break;
                case "IDDFS":
                    setAlgorithmValue(new IDDFS());
                    break;
            }

            String boardSizeDefault = appContext.getResources().getString(R.string.board_size_setting_default);
            setBoardSizeValue(Integer.parseInt(sharedPreferences.getString(this.boardSizeKey, boardSizeDefault)));
        }

        public void saveLocalValuesToPreferences() {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putString(this.algorithmKey, getAlgorithmValue().getClass().getSimpleName());
            editor.putInt(this.boardSizeKey, getBoardSizeValue());
            editor.apply();
        }

        public SearchMethod<SlidingPuzzleNode> getAlgorithmValue() {
            return this.algorithmValue;
        }

        public void setAlgorithmValue(SearchMethod<SlidingPuzzleNode> algorithmValue) {
            this.algorithmValue = algorithmValue;
        }

        public int getBoardSizeValue() {
            return this.boardSizeValue;
        }

        public void setBoardSizeValue(int boardSizeValue) {
            this.boardSizeValue = boardSizeValue;
        }
    }

    public class MainActivityPreferences {
        private final SharedPreferences sharedPreferences;
        private final String boardSolutionKey;
        private final String boardSolutionViewingIndexKey;
        private SearchResult<SlidingPuzzleNode> boardSolutionValue;
        private int boardSolutionViewingIndex;

        private MainActivityPreferences() {
            this.sharedPreferences = appContext.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            this.boardSolutionKey = appContext.getString(R.string.board_solution_preference_key);
            this.boardSolutionViewingIndexKey = appContext.getString(R.string.board_solution_viewing_index_preference_key);
            updateLocalValuesFromPreferences();
        }

        public void updateLocalValuesFromPreferences() {
            this.boardSolutionViewingIndex = this.sharedPreferences.getInt(boardSolutionViewingIndexKey, 0);
            String s = this.sharedPreferences.getString(boardSolutionKey, "");
            TypeToken<SearchResult<SlidingPuzzleNode>> typeToken = new TypeToken<SearchResult<SlidingPuzzleNode>>() {};
            this.boardSolutionValue = gson.fromJson(s, typeToken);
        }

        public void saveLocalValuesToPreferences() {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putString(this.boardSolutionKey, gson.toJson(getBoardSolutionValue()));
            editor.putInt(this.boardSolutionViewingIndexKey, getBoardSolutionViewingIndex());
            editor.apply();
        }

        public SearchResult<SlidingPuzzleNode> getBoardSolutionValue() {
            return boardSolutionValue;
        }

        public void setBoardSolutionValue(SearchResult<SlidingPuzzleNode> boardSolutionValue) {
            this.boardSolutionValue = boardSolutionValue;
        }

        public int getBoardSolutionViewingIndex() {
            return boardSolutionViewingIndex;
        }

        public void setBoardSolutionViewingIndex(int boardSolutionViewingIndex) {
            this.boardSolutionViewingIndex = boardSolutionViewingIndex;
        }
    }
}
