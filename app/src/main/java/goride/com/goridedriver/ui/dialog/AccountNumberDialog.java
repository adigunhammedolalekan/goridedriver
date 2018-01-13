package goride.com.goridedriver.ui.dialog;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import goride.com.goridedriver.R;
import goride.com.goridedriver.entities.BankAccount;
import goride.com.goridedriver.util.Util;

/**
 * Created by Lekan Adigun on 12/25/2017.
 */

public class AccountNumberDialog extends DialogFragment {

    @BindView(R.id.edt_account_name_account_info)
    EditText accountNameEditText;
    @BindView(R.id.edt_account_number_account_info)
    EditText accountNumberEditText;
    @BindView(R.id.bank_name_edt_account_info_dialog)
    EditText bankNameEditText;

    public BankAccount bankAccount;

    public static AccountNumberDialog newInstance(BankAccount account) {

        Bundle args = new Bundle();

        AccountNumberDialog fragment = new AccountNumberDialog();
        fragment.bankAccount = account;
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_bank_account_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        if (bankAccount != null) {
            accountNameEditText.setText(bankAccount.getAccountName());
            accountNumberEditText.setText(bankAccount.getAccountNumber());
            bankNameEditText.setText(bankAccount.getBankName());
        }
    }

    @OnClick(R.id.cancelBtn_account_info) public void onCancelButtonClick() {
        dismiss();
    }

    void toast(String body) {
        Toast.makeText(getActivity(), body, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.saveBtn_account_info) public void onSaveButtonClick() {

        if (Util.empty(bankNameEditText)) {
            toast("Enter bank name");
            return;
        }
        if (Util.empty(accountNameEditText)) {
            toast("Enter account name.");
            return;
        }
        if (Util.empty(accountNumberEditText)) {
            toast("Enter account number");
            return;
        }

        new AlertDialog.Builder(getActivity())
                .setTitle("Confirm Save")
                .setMessage("You cannot edit this information until after 7 days, are you sure you want to continue?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveInfo();
                    }
                })
                .setNegativeButton("NO", null)
                .create()
                .show();
    }
    private void saveInfo() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FirebaseDatabase.getInstance().getReference("drivers_accounts")
                    .child(user.getUid())
                    .setValue(new BankAccount(Util.textOf(accountNameEditText),
                            Util.textOf(accountNumberEditText), Util.textOf(bankNameEditText)));
            dismiss();
            toast("Account Info Saved");
        }
    }
}
