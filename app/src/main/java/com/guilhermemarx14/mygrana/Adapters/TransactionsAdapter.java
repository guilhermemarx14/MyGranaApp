package com.guilhermemarx14.mygrana.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView valueTextView;
        public TextView descriptionTextView;
        public TextView dateTextView;
        public TextView categoryTextView;
        public TextView subcategoryTextView;
        public ImageView payd;
        public ImageView unpayd;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            valueTextView = itemView.findViewById(R.id.valueList);
            descriptionTextView = itemView.findViewById(R.id.descriptionList);
            dateTextView = itemView.findViewById(R.id.dateList);
            categoryTextView = itemView.findViewById(R.id.categoryList);
            payd = itemView.findViewById(R.id.payd);
            unpayd = itemView.findViewById(R.id.unpayd);

        }
    }


    private List<Transaction> mTransactions;
    Context context;
    // Pass in the contact array into the constructor
    public TransactionsAdapter(Context context, List<Transaction> transactions) {
        mTransactions = transactions;
        this.context = context;
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
        Transaction transaction = mTransactions.get(position);

        // Set item views based on your views and data model
        if(transaction.getValue()>=0)
            viewHolder.valueTextView.setTextColor(context.getResources().getColor(R.color.colorAccent));
        else viewHolder.valueTextView.setTextColor(context.getResources().getColor(R.color.colorRed));

        viewHolder.valueTextView.setText(String.format("R$ %.2f", transaction.getValue()));
        viewHolder.descriptionTextView.setText(transaction.getDescription());
        viewHolder.dateTextView.setText(transaction.getDate());
        viewHolder.categoryTextView.setText(transaction.getCategory());
        if(transaction.isPayd()) {
            viewHolder.unpayd.setVisibility(View.GONE);
            viewHolder.payd.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.unpayd.setVisibility(View.VISIBLE);
            viewHolder.payd.setVisibility(View.GONE);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTransactions.size();
    }
}
