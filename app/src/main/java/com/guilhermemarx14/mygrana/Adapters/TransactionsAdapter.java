package com.guilhermemarx14.mygrana.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guilhermemarx14.mygrana.R;
import com.guilhermemarx14.mygrana.RealmObjects.Transaction;

import java.util.List;

/**
 * Created by Guilherme Marx on 2019-05-19
 */
public class TransactionsAdapter extends
        RecyclerView.Adapter<TransactionsAdapter.ViewHolder>{
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.valueList);
        }
    }


    private List<Transaction> mTransactions;

    // Pass in the contact array into the constructor
    public TransactionsAdapter(List<Transaction> transactions) {
        mTransactions = transactions;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public TransactionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_transaction, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TransactionsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Transaction transition = mTransactions.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(String.format("R$ %.2f",transition.getValue()));
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTransactions.size();
    }
}
