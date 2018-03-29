import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 6;
    private String tabTitles[] =
            new String[] { "Tab Uno", "Tab Dos", "Tab Tres", "Tab Cuatro", "Tab Cinco", "Tab Seis"};

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public int getCount() {
        return PAGE_COUNT;
    }

    public Fragment getItem(int position) {

        Fragment f = null;

        switch(position) {
            case 0:
            case 2:
            case 4:
           //     f = List.newInstance();
                break;
            case 1:
            case 3:
            case 5:
          //      f = Fragment2.newInstance();
                break;
        }

        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}