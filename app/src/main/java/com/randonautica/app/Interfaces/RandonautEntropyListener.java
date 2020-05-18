package com.randonautica.app.Interfaces;

import com.randonautica.app.Classes.SingleRecyclerViewLocation;

import java.util.ArrayList;

public interface RandonautEntropyListener{

    public void onData(String GID);
    public void onFailed(ArrayList<SingleRecyclerViewLocation> locationList);

}
