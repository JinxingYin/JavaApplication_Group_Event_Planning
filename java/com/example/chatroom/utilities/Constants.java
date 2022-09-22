package com.example.chatroom.utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_IS_GROUP_MESSAGE = "is group message";
    public static final String KEY_EVENT_NAME = "event name";
    public static final String KEY_EVENT_LIST = "eventList";
    public static final String KEY_USER_PASSWORD = " user password";
    public static final String KEY_USER_EMAIL = " user email";
    public static final String KEY_GROUP_LIST = "group list";
    public static final String KEY_SELECT_TO_EVENT_OR_NOT = "event select";
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER ="user";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP ="timestamp";
    public static final String KEY_COLLECTION_CONVERSATIONS= "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String KEY_EVENT = "event";
    public static final String KEY_EVENT_DESCRIPTION = "event description";
    public static final String KEY_EVENT_DATE = "event date";
    public static final String KEY_EVENT_TIME= "event time";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String KEY_ADDED_TO_GROUP = "added to group";
    public static final String KEY_POLL="poll";
    public static HashMap<String ,String> remoteMsgHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders(){
        if(remoteMsgHeaders ==null){
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAL-FlYxM:APA91bEx7ScutVLgzS-XqV0oPUNs4zYEhjn3nRawOeEw6K2xgoJjqomqOGBSKMdQtMw_JANd_zUMUsDytDpThGP5NEbCHT7q86gGeAiB19QkOg7zMEL2FXcQY2exW2SYl5gETtYLFaD7"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }


}
