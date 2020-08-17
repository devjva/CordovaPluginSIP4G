package com.sip.linphone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.linphone.core.Core;
import org.linphone.core.Call;
import org.linphone.core.ProxyConfig;
import org.linphone.mediastream.Log;

import java.util.Timer;

public class Linphone extends CordovaPlugin  {
    public static Linphone mInstance;
    public static LinphoneMiniManager mLinphoneManager;
    public static Core mLinphoneCore;
    public static Context mContext;
    private static final int RC_MIC_PERM = 2;
    public static Timer mTimer;
    public CallbackContext mListenCallback;
    CordovaInterface cordova;

    public SipManager manager = null;
    public SipProfile me = null;
    public SipAudioCall call = null;


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        this.cordova = cordova;
        mContext = cordova.getActivity().getApplicationContext();
        mLinphoneManager = new LinphoneMiniManager(mContext);
        mLinphoneCore = mLinphoneManager.getLc();
        mInstance = this;
    }

    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
       
          if (action.equals("login")){
                android.util.Log.d("CORE","LOGIN IN");
				  cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                login(args.getString(0), args.getString(1), args.getString(2), callbackContext);
				} catch (Exception e) {Log.d("login error", e.getMessage());}}});
		  return true;}
          else if (action.equals("logout")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                logout(callbackContext);
				} catch (Exception e) {Log.d("logout error", e.getMessage());}}});
		  return true;}
          else if (action.equals("call")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                call(args.getString(0), args.getString(1), callbackContext);
				} catch (Exception e) {Log.d("call error", e.getMessage());}}});
		  return true;}
          else if (action.equals("listenCall")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                listenCall(callbackContext);
				} catch (Exception e) {Log.d("listenCall error", e.getMessage());}}});
		  return true;}
          else if (action.equals("acceptCall")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                acceptCall(args.getString(0), callbackContext);
				} catch (Exception e) {Log.d("acceptCall error", e.getMessage());}}});
		  return true;}
          else if (action.equals("videocall")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                videocall(args.getString(0), args.getString(1), callbackContext);
				} catch (Exception e) {Log.d("videocall error", e.getMessage());}}});
		  return true;}
          else if (action.equals("hangup")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                hangup(callbackContext);
				} catch (Exception e) {Log.d("hangup error", e.getMessage());}}});
		  return true;}
          else if (action.equals("toggleVideo")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                toggleVideo(callbackContext);
				} catch (Exception e) {Log.d("toggleVideo error", e.getMessage());}}});
		  return true;}
          else if (action.equals("toggleSpeaker")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                toggleSpeaker(args.getString(0), callbackContext);
				} catch (Exception e) {Log.d("toggleSpeaker error", e.getMessage());}}});
		  return true;}
          else if (action.equals("toggleMute")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                toggleMute(callbackContext);
				} catch (Exception e) {Log.d("toggleMute error", e.getMessage());}}});
		  return true;}
          else if (action.equals("sendDtmf")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                sendDtmf(args.getString(0), callbackContext);
				} catch (Exception e) {Log.d("sendDtmf error", e.getMessage());}}});
		  return true;}
		  else if (action.equals("getRemoteContact")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                getRemoteContact(callbackContext);
				} catch (Exception e) {Log.d("getRemoteContact error", e.getMessage());}}});
		  return true;}	
		  else if (action.equals("updateRegister")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                updateRegister(callbackContext);
				} catch (Exception e) {Log.d("updateRegister error", e.getMessage());}}});
		  return true;}
		  else if (action.equals("lowBandwidth")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                lowBandwidth(args.getString(0), callbackContext);
				} catch (Exception e) {Log.d("lowBandwidth error", e.getMessage());}}});
		  return true;}
		  else if (action.equals("setMicrophoneVolumeGain")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                setMicrophoneVolumeGain(args.getString(0), callbackContext);
				} catch (Exception e) {Log.d("setMicrophoneVolumeGain error", e.getMessage());}}});
		  return true;}
		  else if (action.equals("setMicGainDb")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                setMicGainDb(args.getString(0), callbackContext);
				} catch (Exception e) {Log.d("setMicGainDb error", e.getMessage());}}});
		  return true;}
		  else if (action.equals("setPlaybackGainDb")){
			    cordova.getThreadPool().execute(new Runnable() {public void run() {try { 
                setPlaybackGainDb(args.getString(0), callbackContext);
				} catch (Exception e) {Log.d("setMicGainDb error", e.getMessage());}}});
		  return true;}
		  
        
        return false;
    }

    public void login(final String username, final String password, final String domain, final CallbackContext callbackContext) {
      if (!cordova.hasPermission(Manifest.permission.RECORD_AUDIO)) {
        cordova.requestPermission(this, RC_MIC_PERM, Manifest.permission.RECORD_AUDIO);
      }

      mLinphoneManager.login(username,password,domain,callbackContext);
    }

  @Override
  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {

  }

    public static synchronized void logout(final CallbackContext callbackContext) {
        try{
            Log.d("logout");
            ProxyConfig[] prxCfgs = mLinphoneManager.getLc().getProxyConfigList();
            final ProxyConfig proxyCfg = prxCfgs[0];
            mLinphoneManager.getLc().removeProxyConfig(proxyCfg);
            Log.d("logout sukses");
            callbackContext.success();
        }catch (Exception e){
            Log.d("Logout error", e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }

    public static synchronized void call(final String address, final String displayName, final CallbackContext callbackContext) {
        try {
			
				mLinphoneManager.call(address, displayName, callbackContext);
			
        }catch (Exception e){
            Log.d("call error", e.getMessage());
        }
    }

    public static synchronized void hangup(final CallbackContext callbackContext) {
        try{
            mLinphoneManager.hangup(callbackContext);
        }catch (Exception e){
            Log.d("hangup error", e.getMessage());
        }
    }

    public static synchronized void listenCall( final CallbackContext callbackContext){
		
        mLinphoneManager.listenCall(callbackContext);
    }

    public static synchronized void acceptCall( final String isAcceptCall, final CallbackContext callbackContext){
        if("true".equals(isAcceptCall)){
			if("true".equals(isAcceptCall)){			
			mLinphoneManager.acceptCall(callbackContext);
			}
		}
        else{
			 mLinphoneManager.terminateCall();
		}
    }

    public static synchronized void videocall(final String address, final String displayName, final CallbackContext callbackContext) {
        try{
            Log.d("incall", address, displayName);
            Intent intent = new Intent(mContext, LinphoneMiniActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("address", address);
            intent.putExtra("displayName", displayName);
            mContext.startActivity(intent);
            Log.d("incall sukses");
            callbackContext.success();
        }catch (Exception e){
            Log.d("incall error", e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }

    public static synchronized void toggleVideo(final CallbackContext callbackContext) {
        try{
            boolean isenabled = mLinphoneManager.toggleEnableCamera();
            callbackContext.success(isenabled ? 1 : 0);
        }catch (Exception e){
            Log.d("toggleVideo error", e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }

    public static synchronized void toggleSpeaker( final String speaker,final CallbackContext callbackContext) {
        try{
            Log.d("toggleSpeaker");
			
			 if("true".equals(speaker)){
				 
				mLinphoneManager.toggleEnableSpeaker(true);
				callbackContext.success("true");
			 }else{
				 mLinphoneManager.toggleEnableSpeaker(false);
				  callbackContext.success("false");
			 }
         
            
        }catch (Exception e){
            Log.d("toggleSpeaker error", e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }

    public static synchronized void toggleMute(final CallbackContext callbackContext) {
        try{
            Log.d("toggleMute");
            boolean isenabled = mLinphoneManager.toggleMute();
            Log.d("toggleMute sukses",isenabled);
            callbackContext.success(isenabled ? 1 : 0);
        }catch (Exception e){
            Log.d("toggleMute error", e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }

    public static synchronized void sendDtmf(final String number, final CallbackContext callbackContext) {
        try{
            Log.d("sendDtmf");
            mLinphoneManager.sendDtmf(number.charAt(0));
            Log.d("sendDtmf sukses",number);
            callbackContext.success();
        } catch (Exception e){
            Log.d("sendDtmf error", e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }
	public static synchronized void updateRegister(final CallbackContext callbackContext) {
        try{
            Log.d("refreshRegisters");
            mLinphoneManager.getLc().refreshRegisters();
           
            callbackContext.success("refreshRegisters");
        } catch (Exception e){
            Log.d("refreshRegisters error", e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }
	public static synchronized void getRemoteContact (final CallbackContext callbackContext) {
	  try {
	  // Log.d("Get Remote Contact");
	   Call call =  mLinphoneManager.getLc().getCurrentCall();
	   callbackContext.success(call.getRemoteAddress().getUsername().toString());
	   //callbackContext.success("0");
	    Log.d("  cordova.getThreadPool().execute(new Runnable() {public void run() {try {   cordova.getThreadPool().execute(new Runnable() {public void run() {try {   cordova.getThreadPool().execute(new Runnable() {public void run() {try { Get Remote Contact "+ call.getRemoteAddress());
	  } catch (Exception e) {
	   Log.d("Update Error", e.getMessage());
	   callbackContext.error(e.getMessage());
	  }
	}	
	
	public static synchronized void lowBandwidth (final String isActive, final CallbackContext callbackContext) {
	  try {
		  
		    if("true".equals(isActive)){
				 callbackContext.success((mLinphoneManager.setLowBandwidth(true))  ? "true" : "false");
			}else{				
				 callbackContext.success((mLinphoneManager.setLowBandwidth(false))  ? "true" : "false");
			}	  
	  } catch (Exception e) {
	   Log.d("lowBandwidth Error", e.getMessage());
	   callbackContext.error(e.getMessage());
	  }
	}
	
	
	public static synchronized void setPlaybackGainDb (final String value, final CallbackContext callbackContext) {
	  try {
		  
		  mLinphoneManager.setPlaybackGainDb(value);
		  callbackContext.success("ok");
			
	  } catch (Exception e) {
	   Log.d("setPlaybackGainDb Error", e.getMessage());
	   callbackContext.error(e.getMessage());
	  }
	}

	public static synchronized void setMicGainDb (final String value, final CallbackContext callbackContext) {
	  try {
		  
		  mLinphoneManager.setMicGainDb(value);
		  callbackContext.success("ok");
			
	  } catch (Exception e) {
	   Log.d("setMicGainDb Error", e.getMessage());
	   callbackContext.error(e.getMessage());
	  }
	}
	
	
	public static synchronized void setMicrophoneVolumeGain(final String value, final CallbackContext callbackContext) {
	  try {
		  
		  mLinphoneManager.setMicrophoneVolumeGain(value);
		  callbackContext.success("ok");
			
	  } catch (Exception e) {
	   Log.d("setMicrophoneVolumeGain Error", e.getMessage());
	   callbackContext.error(e.getMessage());
	  }
	}
	
	
	
}
