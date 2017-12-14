package goride.com.goridedriver.listviews;

import java.util.ArrayList;
import java.util.List;

import goride.com.goridedriver.R;


/**
 * Created by User on 5/19/2017.
 */

public class RecyclerViewHistory {

    private String tripPrice;
    private String tripDate;
    private String tripFrom;
    private String tripTo;






    public String gettripPrice(){
        return tripPrice;
    }

    public void setTripPrice(String tripPrice){
        this.tripPrice = tripPrice;
    }

    public String gettripDate(){

        return tripDate;
    }

    public void setTripDate(String tripDate){

        this.tripDate = tripDate;
    }

    public String getTripFrom(){

        return tripFrom;
    }

    public void setTripFrom(String tripFrom){

        this.tripFrom = tripFrom;
    }

    public String getTripTo(){

        return tripTo;
    }

    public void setTripTo(String tripTo){

        this.tripTo = tripTo;
    }




    public static List<RecyclerViewHistory> getObjectList(){

        List<RecyclerViewHistory> dataList = new ArrayList<>();

        String[] tripFare  = getTripFare();


        for (int i = 0; i < tripFare.length; i++){
            RecyclerViewHistory recyclerViewHistory = new RecyclerViewHistory();

            //natureModel.setImageID(images[i]);
            recyclerViewHistory.setTripPrice(tripFare[i]);
            recyclerViewHistory.setTripDate("17 Aug 2017");
           recyclerViewHistory.setTripFrom("General Hospital Maitama, FCT Abuja");
            recyclerViewHistory.setTripTo("Parks and Garden, Zone 1, Wuse, Abuja");
            dataList.add(recyclerViewHistory);

        }



        return dataList;
    }


    public static String[] getTripFare(){

        String[]  fares = {

                "N2,125.00",
                "N2,125.00",
                "N2,125.00",
                "N2,125.00",
                "N2,125.00",
                "N2,125.00",
                "N2,125.00",




        };

        return fares;
    }
}
