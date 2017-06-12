package edu.scu.chat.Utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by yaojianwang on 6/7/17.
 */

public class Utils {
    public static final String CURRENT_USER_ID = "CURRENT_USER_ID";
    public static final String CONTACT_ID = "CONTACT_ID";

    public static final class Options{
        /**
         * if true option to send location will be available, If the google maps key is empty it wont be available.
         **/
        public static boolean LocationEnabled = true;

        /**
         * if true option to open group chats and add users to chat will be available.
         **/
        public static boolean GroupEnabled = true;

        /**
         * if true the option to open the thread details would be visible in the chat activity.
         **/
        public static boolean ThreadDetailsEnabled = true;

        /**
         * If true images opened in the chat activity will be saved to the user image gallery
         * under the name assigned in the ImageDirName, Also images you pick from gallery and send will be saved.
         * If this is disabled images will be saved to the app cache directory.
         *
         * The message list adapter will first try to load images from internal storage, Enabling this will get
         * Better performance loading images and reduce network use.
         *
         //         * @see #ImageDirName
         * */
        public static boolean SaveImagesToDir = false;

        /**
         * The maximum amounts of lines that will be shown in the notification for incoming messages.
         *
         * Seems to have a problem when showing more then seven lines in lollipop.
         **/
        public static final int MaxInboxNotificationLines = 7;

        /**
         * The string that will be used to format the selected date of birth.
         **/
        public static final String DateOfBirthFormat = "dd/MM/yyyy";
    }

    public static class SystemChecks{
        //check if the device has a camera
        public static boolean checkCameraHardware(Context context) {
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                // this device has a camera
                return true;
            } else {
                // no camera on this device
                return false;
            }
        }
    }
}
