package digital.upbeat.estisharati_user.Utils;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;




public class PinTextWatcher implements TextWatcher {
    private int currentIndex;
    private boolean isFirst = false, isLast = false;
    private String newTypedString = "";
    Context context;
    EditText[] editTexts;

    public PinTextWatcher(Context context, int currentIndex, EditText[] editTexts) {
        this.currentIndex = currentIndex;
        this.context = context;
        this.editTexts = editTexts;

        if (currentIndex == 0)
            this.isFirst = true;
        else if (currentIndex == editTexts.length - 1)
            this.isLast = true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        newTypedString = s.subSequence(start, start + count).toString().trim();


    }

    @Override
    public void afterTextChanged(Editable s) {

        String text = newTypedString;


        /* Detect paste event and set first char */
        if (text.length() > 1)
            text = String.valueOf(text.charAt(0));

        editTexts[currentIndex].removeTextChangedListener(this);
        editTexts[currentIndex].setText(text);
        editTexts[currentIndex].setSelection(text.length());
        editTexts[currentIndex].addTextChangedListener(this);


        for (int editIndex = 0; editIndex < editTexts.length; editIndex++) {
            if (editTexts[editIndex].getText().toString().equalsIgnoreCase("")) {
//                editTexts[editIndex].setBackgroundResource(R.drawable.round_otp_unselected);
            } else {
//                editTexts[editIndex].setBackgroundResource(R.drawable.round_otp_filed);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                editTexts[editIndex].setElevation(0f);
            }
        }


        if (text.length() == 1)
            moveToNext();
        else if (text.length() == 0)
            moveToPrevious();

    }

    private void moveToNext() {

        if (!isLast) {
            editTexts[currentIndex + 1].requestFocus();

//            editTexts[currentIndex + 1].setBackgroundResource(R.drawable.round_otp_selected);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                editTexts[currentIndex + 1].setElevation(6f);
            }

        }

        if (isAllEditTextsFilled() && isLast) { // isLast is optional
            editTexts[currentIndex].clearFocus();
            hideKeyboard(editTexts[currentIndex]);
        }


    }

    private void moveToPrevious() {
        if (!isFirst) {
            editTexts[currentIndex - 1].requestFocus();
//            editTexts[currentIndex - 1].setBackgroundResource(R.drawable.round_otp_selected);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                editTexts[currentIndex - 1].setElevation(6f);
            }

        } else {
            editTexts[currentIndex].requestFocus();
//            editTexts[currentIndex].setBackgroundResource(R.drawable.round_otp_selected);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                editTexts[currentIndex].setElevation(6f);
            }
        }
    }

    private boolean isAllEditTextsFilled() {
        for (EditText editText : editTexts)
            if (editText.getText().toString().trim().length() == 0)
                return false;
        return true;
    }

    public void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        for (int editIndex = 0; editIndex < editTexts.length; editIndex++) {
            if (editTexts[editIndex].getText().toString().equalsIgnoreCase("")) {
//                editTexts[editIndex].setBackgroundResource(R.drawable.round_otp_unselected);
            } else {
//                editTexts[editIndex].setBackgroundResource(R.drawable.round_otp_filed);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                editTexts[editIndex].setElevation(0f);
            }
        }


    }

}
