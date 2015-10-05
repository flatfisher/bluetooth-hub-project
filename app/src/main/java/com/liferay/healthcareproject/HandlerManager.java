package com.liferay.healthcareproject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by flatfisher on 9/24/15.
 */
public class HandlerManager {

    private HandlerManager(){}

    public static void addView(final ViewGroup viewGroup,final View view){

        new Thread(new Runnable() {

            public void run() {

                viewGroup.post(new Runnable() {

                    public void run() {

                        viewGroup.addView(view);

                    }
                });
            }

        }).start();
    }

    public static void setVisibility(final View view,final int status){

        new Thread(new Runnable() {

            public void run() {

                view.post(new Runnable() {

                    public void run() {

                        view.setVisibility(status);

                    }
                });
            }

        }).start();
    }

    public static void setText(final TextView textView,final String text){

        new Thread(new Runnable() {

            public void run() {

                textView.post(new Runnable() {

                    public void run() {

                        textView.setText(text);

                    }
                });
            }

        }).start();
    }
}
