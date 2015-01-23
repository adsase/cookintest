package dam2.sixapp.cookin.tabSwipeAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class TabsPagerAdapter extends FragmentPagerAdapter {
 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            // Activity fragment favoritos
            return new FavoritesFragment();
        case 1:
        	// Activity fragment pendiente
            return new ToDoFragment();
        case 2:
        	// Activity fragment hecho
            return new DoneFragment();
        case 3:
        	// Activity fragment premium
            return new PremiumFragment();
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // Devuelve el numero de tabs que hay
        return 4;
    }
 
}
