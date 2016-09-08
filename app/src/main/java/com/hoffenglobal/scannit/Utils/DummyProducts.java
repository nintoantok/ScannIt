package com.hoffenglobal.scannit.Utils;

import java.util.HashMap;

/**
 * Created by nintoantok on 06/09/16.
 */
public class DummyProducts {


    public static HashMap<String, String> getProducts(){

        HashMap<String, String> dummyProductList = new HashMap<>();

        dummyProductList.put("02754154" , "Niagra Drinking Water");
        dummyProductList.put("Dummy Val2" , "Dummy Name2");
        dummyProductList.put("Dummy Val3" , "Dummy Name3");
        //Add Dummy Values
        return dummyProductList;

    }

}
