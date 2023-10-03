package de.androidcrypto.storagemanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class UnitImagesRVAdapter extends RecyclerView.Adapter<UnitImagesRVAdapter.ViewHolder> {

    // variable for our array list and context
    private ArrayList<StorageUnitModel> unitModelArrayList;
    private Context context;

    // constructor
    public UnitImagesRVAdapter(ArrayList<StorageUnitModel> unitModelArrayList, Context context) {
        this.unitModelArrayList = unitModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // on below line we are inflating our layout
        // file for our recycler view items.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_images_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // on below line we are setting data
        // to our views of recycler view item.
        StorageUnitModel model = unitModelArrayList.get(position);
        holder.unitNumberTv.setText(model.getUnitNumber());
        holder.unitShortContentTv.setText(model.getUnitShortContent());
        //holder.unitTypeTv.setText(model.getUnitType());
        //holder.unitWeightTv.setText(model.getUnitWeight());
        //holder.unitPlaceTv.setText(model.getUnitPlace());

        String filename1 = model.getUnitImageFilename1();
        if (!TextUtils.isEmpty(filename1)) {
            FileInputStream fis = null;
            Bitmap bitmap = null;
            try {
                File dir=new File(context.getFilesDir(), Constants.IMAGE_FOLDER_THUMBNAIL);
                fis = new FileInputStream(new File(dir, filename1));
                if(fis !=null && fis.available() > 0) {
                    bitmap = BitmapFactory.decodeStream(fis);
                    holder.unitThumbnailImage1.setImageBitmap(bitmap);
                } else {
                    // do nothing
                    //input stream has not much data to convert into  Bitmap
                }
            } catch (IOException e) {
                // do nothing
                // TODO Auto-generated catch block
                //e.printStackTrace();
                //return;
            }
        }

        String filename2 = model.getUnitImageFilename2();
        if (!TextUtils.isEmpty(filename2)) {
            FileInputStream fis = null;
            Bitmap bitmap = null;
            try {
                File dir=new File(context.getFilesDir(), Constants.IMAGE_FOLDER_THUMBNAIL);
                fis = new FileInputStream(new File(dir, filename2));
                if(fis !=null && fis.available() > 0) {
                    bitmap = BitmapFactory.decodeStream(fis);
                    holder.unitThumbnailImage2.setImageBitmap(bitmap);
                } else {
                    // do nothing
                    //input stream has not much data to convert into  Bitmap
                }
            } catch (IOException e) {
                // do nothing
                // TODO Auto-generated catch block
                //e.printStackTrace();
                //return;
            }
        }

        String filename3 = model.getUnitImageFilename3();
        if (!TextUtils.isEmpty(filename3)) {
            FileInputStream fis = null;
            Bitmap bitmap = null;
            try {
                File dir=new File(context.getFilesDir(), Constants.IMAGE_FOLDER_THUMBNAIL);
                fis = new FileInputStream(new File(dir, filename3));
                if(fis !=null && fis.available() > 0) {
                    bitmap = BitmapFactory.decodeStream(fis);
                    holder.unitThumbnailImage3.setImageBitmap(bitmap);
                } else {
                    // do nothing
                    //input stream has not much data to convert into  Bitmap
                }
            } catch (IOException e) {
                // do nothing
                // TODO Auto-generated catch block
                //e.printStackTrace();
                //return;
            }
        }

        //String entryFavourite = model.getEntryFavourite();
        //holder.entryFavouriteTV.setText(entryFavourite);
        /*
        if (entryFavourite.equals("1")) {
            holder.entryFavouriteIV.setImageResource(R.drawable.ic_baseline_star_rate_24);
        } else {
            holder.entryFavouriteIV.setImageResource(R.drawable.ic_baseline_star_outline_24);
        }

         */
        String unitId = model.getUnitId();

        /*
        // long click means copy the entryPassword
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // There are other constants available in HapticFeedbackConstants like VIRTUAL_KEY, KEYBOARD_TAP
                //HapticFeedbackConstants.CONFIRM
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                //v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

                // copy to clipboard
                // Gets a handle to the clipboard service.
                ClipboardManager clipboard = (ClipboardManager)
                        context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", Cryptography.decryptStringAesGcmFromBase64(model.getEntryLoginPassword()));
                // Set the clipboard's primary clip.
                clipboard.setPrimaryClip(clip);
                Toast.makeText(v.getContext(), "Passwort kopiert", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

         */

        // below line is to add on click listener for our recycler view item.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are calling an intent.
                Intent i = new Intent(context, UpdateUnitActivity.class);
                // below we are passing all our values.
                i.putExtra("unitNumber", model.getUnitNumber());
                i.putExtra("unitShortContent", model.getUnitShortContent());
                i.putExtra("unitContent", model.getUnitContent());
                i.putExtra("unitType", model.getUnitType());
                i.putExtra("unitWeight", model.getUnitWeight());
                i.putExtra("unitPlace", model.getUnitPlace());
                i.putExtra("unitRoomNr", model.getUnitRoomNr());
                i.putExtra("unitLastEdit", model.getUnitLastEdit());
                i.putExtra("unitIdServer", model.getUnitIdServer());
                i.putExtra("unitTagUid1", model.getUnitTagUid1());
                i.putExtra("unitTagUid2", model.getUnitTagUid2());
                i.putExtra("unitTagUid3", model.getUnitTagUid3());
                i.putExtra("unitImageFilename1", model.getUnitImageFilename1());
                i.putExtra("unitImageFilename2", model.getUnitImageFilename2());
                i.putExtra("unitImageFilename3", model.getUnitImageFilename3());
                i.putExtra("unitId", model.getUnitId());
                context.startActivity(i);

                /*
                unitNumber.setText(getIntent().getStringExtra("unitNumber"));
        unitShortContent.setText(getIntent().getStringExtra("unitShortContent"));
        unitContent.setText(getIntent().getStringExtra("unitContent"));
        unitType.setText(getIntent().getStringExtra("unitType"));
        unitWeight.setText(getIntent().getStringExtra("unitWeight"));
        unitPlace.setText(getIntent().getStringExtra("unitPlace"));
        unitRoom.setText(getIntent().getStringExtra("unitRoom"));
        unitLastEdit.setText(getIntent().getStringExtra("unitLastEdit"));
        unitExternalId.setText(getIntent().getStringExtra("unitIdServer"));
        unitTagUid1.setText(getIntent().getStringExtra("unitTagUid1"));
        unitTagUid2.setText(getIntent().getStringExtra("unitTagUid2"));
        unitTagUid3.setText(getIntent().getStringExtra("unitTagUid3"));
                 */

            }
        });
    }

    @Override
    public int getItemCount() {
        // returning the size of our array list
        return unitModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our text views.
        private TextView unitNumberTv, unitShortContentTv, unitTypeTv, unitWeightTv, unitPlaceTv, unitIdTv;
        private ImageView unitThumbnailImage1, unitThumbnailImage2, unitThumbnailImage3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views
            unitNumberTv = itemView.findViewById(R.id.tvUnitNumber);
            unitShortContentTv = itemView.findViewById(R.id.tvUnitShortContent);
            //unitTypeTv = itemView.findViewById(R.id.tvUnitType);
            //unitWeightTv = itemView.findViewById(R.id.tvUnitWeight);
            //unitPlaceTv = itemView.findViewById(R.id.tvUnitPlace);

            unitThumbnailImage1 = itemView.findViewById(R.id.ivThumbnailImage1);
            unitThumbnailImage2 = itemView.findViewById(R.id.ivThumbnailImage2);
            unitThumbnailImage3 = itemView.findViewById(R.id.ivThumbnailImage3);


            // ausgefüllt app:srcCompat="@drawable/ic_baseline_star_rate_24" />
            // leer       app:srcCompat="@drawable/ic_baseline_star_outline_24" />

        }
    }
}

