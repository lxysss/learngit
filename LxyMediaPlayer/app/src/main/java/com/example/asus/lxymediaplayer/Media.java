package com.example.asus.lxymediaplayer;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ASUS on 2017/12/18.
 */

public class Media {

      private String MediaPath;
      private String MediaTitle;
      private String MediaSize;
      private String MediaDuration;
      private String MediaAdded;



      public Media(String MediaPath,String MediaTitle,String MediaSize,String MediaDuration,String MediaAdded){
              this.MediaPath=MediaPath;
              this.MediaTitle=MediaTitle;

              DecimalFormat  df= new DecimalFormat("######0.00");    //两位小数
              this.MediaSize=String.valueOf(df.format(Double.parseDouble(MediaSize)/(1024*1024)));

              int h=(Integer.parseInt(MediaDuration)/1000)/3600;
              int m=(Integer.parseInt(MediaDuration)/1000-h*3600)/60;
              int s=Integer.parseInt(MediaDuration)/1000-h*3600-m*60;
              this.MediaDuration=String.valueOf(h)+":"+String.valueOf(m)+":"+String.valueOf(s);

              SimpleDateFormat sf = new SimpleDateFormat("yy年MM月dd日HH时mm分");
              Date d = new Date(Long.valueOf(MediaAdded)*1000);
              this.MediaAdded = sf.format(d);
          }

      public String getMediaTitle(){
          return MediaTitle;
      }
      public void setMediaTitle(String MediaTitle){
          this.MediaTitle=MediaTitle;
      }

      public String getMediaSize(){
          return MediaSize;
      }
      public void setMediaSize(String MediaSize){
          this.MediaSize=String.valueOf(Integer.getInteger(MediaSize)/(1024*1024));
      }

      public  String getMediaDuration(){
          return MediaDuration;
      }
      public void setMediaDuration(String MediaDuration){
          this.MediaDuration=MediaDuration;
      }

      public String getMediaPath(){
          return MediaPath;
      }
      public void setMdiaPath(String MediaiPath){
          this.MediaPath=MediaPath;
      }

      public String getMediaAdded(){
          return MediaAdded;
      }
      public void setMediaAdded(String MediaAdded){
          SimpleDateFormat sf = new SimpleDateFormat("yy年MM月dd日HH时mm分");
          Date d = new Date(Long.valueOf(MediaAdded)*1000);
          this.MediaAdded = sf.format(d);
      }

   /*   public Bitmap getThumb(){
          this.thumb= ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
          return this.thumb;
      }

     public void  setThumb(){
         this.thumb= ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
     }
*/
}
