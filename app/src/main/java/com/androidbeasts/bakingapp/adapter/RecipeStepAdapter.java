package com.androidbeasts.bakingapp.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidbeasts.bakingapp.R;
import com.androidbeasts.bakingapp.model.Steps;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/*Adapter class for steps list*/
public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.ViewHolder> {

    private ArrayList<Steps> recipeStepsArrayList;
    private Context context;
    private int mNoOfItems;
    final private OnListItemClickListener onListItemClickListener;

    public interface OnListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public RecipeStepAdapter(ArrayList<Steps> recipeStepsArrayList, Context context, int mNoOfItems, OnListItemClickListener listItemClickListener) {
        this.recipeStepsArrayList = new ArrayList<>();
        this.recipeStepsArrayList = recipeStepsArrayList;
        this.context = context;
        this.mNoOfItems = mNoOfItems;
        this.onListItemClickListener = listItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem = R.layout.recipe_steps_list_item;
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
        @Nullable
        @BindView(R.id.step_desc)
        TextView stepDescriptionTv;

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
            Steps steps = recipeStepsArrayList.get(listIndex);
            stepDescriptionTv.setText(steps.getShortDescription());
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

}

