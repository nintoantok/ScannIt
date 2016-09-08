package com.hoffenglobal.scannit.Utils;

import java.util.HashMap;

/**
 * Created by nintoantok on 06/09/16.
 */
public class DummyProducts {


    public static HashMap<String, String> getProducts(){

        HashMap<String, String> dummyProductList = new HashMap<>();

        dummyProductList.put("6297000229027" , "Britin Biscuits");
        dummyProductList.put("6297000229003" , "Chicken Puffs");
        dummyProductList.put("6297000229010" , "App Juice");
        //Add Dummy Values
        return dummyProductList;

    }

}
