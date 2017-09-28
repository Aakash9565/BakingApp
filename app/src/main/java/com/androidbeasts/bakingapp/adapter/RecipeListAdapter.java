package com.androidbeasts.bakingapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidbeasts.bakingapp.R;
import com.androidbeasts.bakingapp.model.Recipe;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/*Adapter class for recipe list*/
public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {

    private ArrayList<Recipe> recipeArrayList;
    private Context context;
    private int mNoOfItems;
    final private OnListItemClickListener onListItemClickListener;

    public interface OnListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public RecipeListAdapter(ArrayList<Recipe> recipeArrayList, Context context, int mNoOfItems, OnListItemClickListener listItemClickListener) {
        this.recipeArrayList = new ArrayList<>();
        this.recipeArrayList = recipeArrayList;
        this.context = context;
        this.mNoOfItems = mNoOfItems;
        this.onListItemClickListener = listItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem;
        boolean is_phone = context.getResources().getBoolean(R.bool.is_phone);
        if (is_phone) {
            layoutIdForListItem = R.layout.recipe_list_item;
        } else {
            layoutIdForListItem = R.layout.recipe_grid_item;
        }
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNoOfItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_name)
        TextView recipeNameTv;
        @BindView(R.id.cooking_icon)
        ImageView recipeIconIv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         *
         * @param listIndex Position of the item in the list
         */
        void bind(int listIndex) {
            Recipe recipe = recipeArrayList.get(listIndex);
            recipeNameTv.setText(recipe.getRecipe_name());

            //Glide.with(itemView).load(recipeArrayList.get(listIndex).getStepsArrayList().get().getVideoURL()).into(recipeIconIv);
//            Drawable verticalImage = null;
//
//                verticalImage = new BitmapDrawable(context.getResources(), retriveVideoFrameFromVideo(recipeArrayList.get(listIndex).getStepsArrayList().get(0).getVideoURL()));

//            recipeIconIv.setImageDrawable(verticalImage);
        }

        /**
         * Called whenever a user clicks on an item in the list.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            onListItemClickListener.onListItemClick(clickedPosition);
        }
    }


    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
}
