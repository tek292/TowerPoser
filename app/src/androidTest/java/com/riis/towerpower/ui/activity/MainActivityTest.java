package com.riis.towerpower.ui.activity;

import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;

import com.riis.towerpower.R;
import com.riis.towerpower.ui.fragment.TowerListFragment;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private MainActivity mActivity;
    private TowerListFragment mFragment;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

        mActivity = getActivity();

//        mFragment = new TowerListFragment();
//
//        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.tower_list_fragment, mFragment, "listFragment");
//        transaction.commit();
//        context = this.getInstrumentation().getTargetContext().getApplicationContext();
//
//        ObjectGraph objectGraph= ObjectGraph.create(new HomeScreenTestObjectGraph(context));
//        DaggerApplication myapp = (DaggerApplication) context;
//        myapp.setDisasterAppObjectGraph(objectGraph);
//
//        contactListDisplay = (ListView) disasterAppActivity.findViewById(R.id.contactListDisplay);
    }

    public void testLayout() {
        assertNotNull(mActivity.findViewById(R.id.tower_list_fragment));
        assertNotNull(mActivity.findViewById(R.id.no_data_textview));
    }
}
