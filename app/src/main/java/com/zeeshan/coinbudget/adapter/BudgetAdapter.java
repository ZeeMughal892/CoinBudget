package com.zeeshan.coinbudget.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zeeshan.coinbudget.MainDashboard;
import com.zeeshan.coinbudget.R;
import com.zeeshan.coinbudget.model.Budget;
import com.zeeshan.coinbudget.model.BudgetModel;
import com.zeeshan.coinbudget.model.ExpenseOverview;
import com.zeeshan.coinbudget.model.User;

import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.MyViewHolder> {

    private List<Budget> budgetList;
    DatabaseReference databaseUser;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    String userCurrency;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtExpenseName, txtDueDate, txtExpense, txtIncome, txtBalance;

        MyViewHolder(View itemView) {
            super(itemView);
            txtExpenseName = itemView.findViewById(R.id.txtExpenseName);
            txtDueDate = itemView.findViewById(R.id.txtDueDate);
            txtExpense = itemView.findViewById(R.id.txtExpenseAmount);
            txtIncome = itemView.findViewById(R.id.txtIncome);
            txtBalance = itemView.findViewById(R.id.txtBalance);
        }
    }

    public BudgetAdapter(List<Budget> budgetList) {
        this.budgetList = budgetList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_budget_summary, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);
       firebaseAuth =FirebaseAuth.getInstance();
       firebaseUser =firebaseAuth.getCurrentUser();
        databaseUser.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userCurrency = user.currency;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(itemView.getContext(),databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Budget budget = budgetList.get(position);

        String expense = userCurrency + " " + budget.getExpense().get(position);
        String income = userCurrency + " " + budget.getIncome().get(position);

        holder.txtExpenseName.setText(budget.getExpenseName().get(position));
        holder.txtDueDate.setText(budget.getDueDate().get(position));
        holder.txtExpense.setText(expense);
        holder.txtIncome.setText(income);

    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }
}
