package estisharatibussiness.users.com.UtilsClasses;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class PinOnKeyListener implements View.OnKeyListener {
    private final int currentIndex;
    private final Context context;
    private final EditText[] editTexts;

  public PinOnKeyListener(Context context, int currentIndex, EditText[] editTexts) {
        this.currentIndex = currentIndex;
        this.context = context;
        this.editTexts = editTexts;

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (editTexts[currentIndex].getText().toString().isEmpty() && currentIndex != 0)
                editTexts[currentIndex - 1].requestFocus();
        }
        return false;
    }

}

