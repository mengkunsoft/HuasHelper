package cn.mkblog.huashelper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import cn.mkblog.huashelper.R;
import cn.mkblog.huashelper.tool.DataSave;

public class WifiSetAty extends BaseAty {

    private EditText passWord, userName;
    private Spinner ispSelect;
    private Button btnSave;

    private String[] ispName = {"中国移动", "中国联通", "中国电信", "校园网"};
    private String[] ispCode = {"@cmcc", "@unicom", "@telecom", ""};

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_wifi:    // 保存账号信息
                save();
                break;
            default:
        }
    }

    @Override
    public void initParams(Bundle params) {
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_wifi_set;
    }

    @Override
    public void initView(View view) {
        passWord = $(R.id.et_wifi_pw);
        userName = $(R.id.et_wifi_user);
        ispSelect = $(R.id.wifi_isp_select);
        btnSave = $(R.id.btn_save_wifi);
    }

    @Override
    public void setListener() {
        btnSave.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        setTitle("校园网账号设置");

        // 填充运营商选择框
        ArrayAdapter<String> ispAdapter = new ArrayAdapter<>(this, R.layout.spiner_text_item, ispName);
        ispAdapter.setDropDownViewResource(R.layout.simple_list_item);
        ispSelect.setAdapter(ispAdapter);

        // 获取并填充用户保存的数据
        initData();

        // 默认返回信息：未发生改变
        Intent mIntent = new Intent();
        mIntent.putExtra("changed", false);
        this.setResult(WifiAty.CHANGE_WIFI_CALLBACK, mIntent);
    }

    // 获取用户保存的数据
    private void initData() {
        String wifiUserName = (String) DataSave.get(WifiSetAty.this, "wifiUserName", "");
        if (wifiUserName == null || wifiUserName.equals("")) {
            // 尝试获取教务系统中的学号
            wifiUserName = (String) DataSave.get(WifiSetAty.this, "stu_id", "");
        }
        userName.setText(wifiUserName);

        String wifiPassWord = (String) DataSave.get(WifiSetAty.this, "wifiPassWord", "");
        passWord.setText(wifiPassWord);

        String wifiIsp = (String) DataSave.get(WifiSetAty.this, "wifiIsp", "@cmcc");
        for (int i = 0; i < ispCode.length; i++) {
            if (ispCode[i].equals(wifiIsp)) {
                ispSelect.setSelection(i);
            }
        }

        // 自动焦点
        if (userName.getText().toString().equals("")) {
            userName.requestFocus();
        } else {
            passWord.requestFocus();
        }
    }

    // 保存数据
    private void save() {
        // 获取输入信息
        String wifiUserName = userName.getText().toString();
        if (wifiUserName.equals("")) {
            showToast("请输入校园网账号");
            userName.requestFocus();    // 设置焦点
            return;
        }

        String wifiPassWord = passWord.getText().toString();
        if (wifiPassWord.equals("")) {
            showToast("请输入校园网密码");
            passWord.requestFocus();    // 设置焦点
            return;
        }

        int ispID = (int) ispSelect.getSelectedItemId();

        // 保存数据
        DataSave.put(WifiSetAty.this, "wifiUserName", wifiUserName);
        DataSave.put(WifiSetAty.this, "wifiPassWord", wifiPassWord);
        DataSave.put(WifiSetAty.this, "wifiIsp", ispCode[ispID]);

        showToast("保存成功！");

        // 返回信息：已发生改变
        Intent mIntent = new Intent();
        mIntent.putExtra("changed", true);
        this.setResult(WifiAty.CHANGE_WIFI_CALLBACK, mIntent);

        finish();
    }

}
