package digital.upbeat.estisharati_user.Utils;

// Created by Surya on 11-01-2017.


/* A PageIndicator is responsible to show an visual indicator on the total views number and the current visible view.*/

import androidx.viewpager.widget.ViewPager;

public interface PageIndicator extends ViewPager.OnPageChangeListener {

    // Bind the indicator to a ViewPager.
    void setViewPager(ViewPager view);

    // Bind the indicator to a ViewPager.

    void setViewPager(ViewPager view, int initialPosition);

    //Set the current page of both the ViewPager and indicator

    void setCurrentItem(int item);

    //Set a page change listener which will receive forwarded events.

    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);

    // Notify the indicator that the fragment list has changed.

    void notifyDataSetChanged();
}