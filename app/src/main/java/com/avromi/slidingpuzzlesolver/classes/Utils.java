package com.avromi.slidingpuzzlesolver.classes;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;
import com.avromi.slidingpuzzlesolver.R;

public class Utils {
    private final Context context;
    private final int mDefaultMargin;
    private final int mEditTextMinWidth;
    private final int mEditTextMinHeight;
    private final int mEditTextMaxMargin;
    private final int mCellMaxHeight;
    private final int mCellMaxWidth;

    public Utils(Context context) {
        this.context = context;
        this.mDefaultMargin = context.getResources()
                .getDimensionPixelSize(R.dimen.default_margin);
        this.mEditTextMinWidth = context.getResources()
                .getDimensionPixelSize(R.dimen.puzzle_board_cell_edit_text_min_width);
        this.mEditTextMinHeight = context.getResources()
                .getDimensionPixelSize(R.dimen.puzzle_board_cell_edit_text_min_height);
        this.mEditTextMaxMargin = context.getResources()
                .getDimensionPixelSize(R.dimen.puzzle_board_cell_edit_text_max_margin);
        this.mCellMaxHeight = context.getResources()
                .getDimensionPixelSize(R.dimen.puzzle_board_cell_max_height);
        this.mCellMaxWidth = context.getResources()
                .getDimensionPixelSize(R.dimen.puzzle_board_cell_max_width);
    }

    public MaterialCardView getNewPuzzleBoardCell(
            int row, int col, int minCellNumericValue, int maxCellNumericValue) {
        MaterialCardView cell = new MaterialCardView(context);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setGravity(Gravity.FILL);
        params.rowSpec = GridLayout.spec(row, 1f);
        params.columnSpec = GridLayout.spec(col, 1f);
        params.width = GridLayout.LayoutParams.WRAP_CONTENT;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;

        params.setMargins(mDefaultMargin, mDefaultMargin, mDefaultMargin, mDefaultMargin);
        cell.setLayoutParams(params);

        EditText editText = getEditTextForCell(minCellNumericValue, maxCellNumericValue);
        cell.addView(editText);

        return cell;
    }

    @NonNull
    private EditText getEditTextForCell(int minCellNumericValue, int maxCellNumericValue) {
        EditText editText = new EditText(context);

        int editTextMargin = -Math.max(mEditTextMinHeight, mEditTextMinWidth);  // todo look into negative margins

        FrameLayout.LayoutParams editTextParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        editTextParams.gravity = Gravity.CENTER;
        editTextParams.setMargins(editTextMargin, editTextMargin, editTextMargin, editTextMargin);
        editText.setLayoutParams(editTextParams);

        editText.setMinimumHeight(mEditTextMinHeight);
        editText.setMinWidth(mEditTextMinWidth);
        editText.setGravity(Gravity.CENTER);
        editText.setHint("");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[]{
                getRangeLimitInputFilter(minCellNumericValue, maxCellNumericValue)
        });
        return editText;
    }

    @NonNull
    private static InputFilter getRangeLimitInputFilter(int minCellNumericValue, int maxCellNumericValue) {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                //noinspection EmptyCatchBlock
                try {
                    int input = Integer.parseInt(dest.subSequence(0, dstart).toString() + source + dest.subSequence(dend, dest.length()));
                    if (isInRange(minCellNumericValue, maxCellNumericValue, input))
                        return null;
                } catch (NumberFormatException nfe) {
                }
                return "";
            }

            private boolean isInRange(int a, int b, int c) {
                return b > a ? c >= a && c <= b : c >= b && c <= a;
            }
        };
    }

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
