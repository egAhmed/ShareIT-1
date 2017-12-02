package matheus.costa.shareit.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Matheus on 02/12/2017.
 */

public class InterfaceUtils {

    public static int getScreenWidth(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        Display display = windowManager.getDefaultDisplay();
        display.getSize(point);
        return point.x;
    }



    public static int getScreenHeight(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        Display display = windowManager.getDefaultDisplay();
        display.getSize(point);
        return point.y;
    }

}
