package com.incar.rkylog.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class SystemUtil {
	private static final String TAG = "SystemUtil";
	public static int execShellCmdForStatue(String command) {
		int status = -1;
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = "";
			while((s = bufferedReader.readLine()) != null){
				Log.d(TAG, " >>>> " + s);
			}
			status = p.waitFor();
			Log.d(TAG, " ________________----------- command: " + command + "    status = " + status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;

	}

	public static String execShellCmd(String command) {
		String result = "";
		Log.i("execShellCmd", command);
		try {
			Process process = Runtime.getRuntime().exec(command + "\n");
			DataOutputStream stdin = new DataOutputStream(
					process.getOutputStream());
			DataInputStream stdout = new DataInputStream(
					process.getInputStream());
			DataInputStream stderr = new DataInputStream(
					process.getErrorStream());
			String line;
			while ((line = stdout.readLine()) != null) {
				result += line + "\n";
			}
			if (result.length() > 0) {
				result = result.substring(0, result.length() - 1);
			}
			while ((line = stderr.readLine()) != null) {
				Log.e("EXEC", line);
			}
			process.waitFor();
		} catch (Exception e) {
			e.getMessage();
		}
		Log.d(TAG, "execShellCmd result: "+result);
		return result;
	}



}
