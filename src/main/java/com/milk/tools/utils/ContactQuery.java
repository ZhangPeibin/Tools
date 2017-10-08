package com.milk.tools.utils;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wiki on 16/3/1.
 */
public class ContactQuery {


    private OnQueryComplete mOnQueryComplete;

    private Context mContext;

    /**
     * 用来获取手机通讯录的URL
     */
    private static final Uri url = Uri.parse("content://com.android.contacts/data/phones");

    /**
     * 手机联系人所需要的数据
     */
    String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY };


    private static final String S_SORT_KEY = "sort_key COLLATE LOCALIZED asc";

    private MyAsyncQueryHandler mMyAsyncQueryHandler;

    private ContactQuery(Context context){
        mContext = context;
        mMyAsyncQueryHandler = new MyAsyncQueryHandler(context.getContentResolver());
    }

    public static ContactQuery getInstance(Context context){
        return new ContactQuery(context);
    }

    public void startQuery(OnQueryComplete onQueryComplete){
        this.mOnQueryComplete = onQueryComplete;
        mMyAsyncQueryHandler.startQuery(0,null,url ,projection,null,null,S_SORT_KEY);
    }


    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                List<Phone> list = new ArrayList<Phone>();
                List<String> alpha = new ArrayList<>();
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2).startsWith("+86")?
                            cursor.getString(2).substring(3):cursor.getString(2);

                    String sortKey = cursor.getString(3);

                    String firstLetters = StringUtil.getAlpha(sortKey);

                    if(!alpha.contains(firstLetters)){
                        alpha.add(firstLetters);
                    }

                    Phone phone = new Phone(sortKey,number,name,firstLetters);
                    phone.sectionType = firstLetters;
                    list.add(phone);
                }

                if(mOnQueryComplete!=null)
                    mOnQueryComplete.complete(list,alpha);
            }
        }
    }

    public interface OnQueryComplete{
        void complete(List<Phone> phones, List<String> alphas);
    }


    public class Phone{
        public String name;
        public String phone;
        public String sortKey;
        public String firstLetters;
        public String sectionType;

        //ture 为head false 为item
        public boolean isSection;

        public Phone(String sortKey, String phone, String name, String firstLetters) {
            this.sortKey = sortKey;
            this.phone = phone;
            this.name = name;
            this.firstLetters = firstLetters;
        }

        public Phone(){

        }

        public void setSectionType(String sectionType) {
            this.sectionType = sectionType;
        }

        public void setFirstLetters(String firstLetters) {
            this.firstLetters = firstLetters;
        }
    }

}
