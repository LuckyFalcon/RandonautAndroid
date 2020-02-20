package com.randonautica.app.Interfaces;

import com.randonautica.app.Classes.SingleRecyclerViewLocation;

import java.util.ArrayList;

public interface RandonautAttractorListener{

    public void onData(ArrayList<SingleRecyclerViewLocation> GID);
    public void onFailed();

}
