#import <AudioToolbox/AudioToolbox.h>
#import "Linphone.h"
#include "linphone/lpconfig.h"
#include "linphone/linphonecore.h"
#include "linphone/linphonecore_utils.h"
@implementation SipModule

@synthesize lc;
@synthesize call ;

static bool_t running=TRUE;
RCTResponseSenderBlock loginCallBackID ;
RCTResponseSenderBlock callCallBackID ;
NSString *RemoteAddress ;
static bool_t isspeaker=TRUE;
static NSTimer *tListen;




// To export a module named RCTCalendarModule
RCT_EXPORT_MODULE(Sip);

+ (BOOL)requiresMainQueueSetup
{
    return YES;
}

static void stop(int signum){
    running=false;
}


static void registration_state_changed(struct _LinphoneCore *lc, LinphoneProxyConfig *cfg, LinphoneRegistrationState cstate, const char *message){
    
    
    
    //Linphone *neco = [ Linphone new];
    if( cstate == LinphoneRegistrationFailed){
        NSLog( @"RegistrationFailed ###### STATUS");
        loginCallBackID(@[@"RegistrationFailed"]);

        
        
    }
    else if(cstate == LinphoneRegistrationOk){
        //Start Listen
      NSLog( @"RegistrationSuccess ###### STATUS");
    loginCallBackID(@[ @"RegistrationSuccess"]);
       
        
        
    }
    printf("New registration state %s for user id [%s] at proxy [%s]\n"
                              ,linphone_registration_state_to_string(cstate)
                              ,linphone_proxy_config_get_identity(cfg)
                              ,linphone_proxy_config_get_addr(cfg));
}


static void call_state_changed(LinphoneCore *lc, LinphoneCall *call, LinphoneCallState cstate, const char *msg){
    
    if(cstate == LinphoneCallError ){
    
    callCallBackID(@[@"Error"]);
        
        call = NULL;
    }
    if(cstate == LinphoneCallConnected){
    
        linphone_call_enable_echo_cancellation(call, true);
        linphone_call_enable_echo_limiter(call, true);
        callCallBackID(@[@"Connected"]);
    }
    if(cstate == LinphoneCallEnd){
        
        call = NULL;
       
        callCallBackID(@[@"End"]);
    
        isspeaker = true;
        UInt32 audioRouteOverride = kAudioSessionOverrideAudioRoute_None;
        AudioSessionSetProperty(kAudioSessionProperty_OverrideAudioRoute, sizeof(audioRouteOverride),
                                &audioRouteOverride);
        
        
      
        
    }
    if(cstate == LinphoneCallIncomingReceived){
    callCallBackID(@[@"Incoming"]);
        
        
    }
}


RCT_EXPORT_METHOD (acceptCall:(bool*)isAccept callback:(RCTResponseSenderBlock)callback)
{
  
    if( isAccept == TRUE){
        
        linphone_core_accept_call( lc, call);
        callCallBackID = callback;
    }
    else{
        
        linphone_core_terminate_call( lc, call);
    }
}

RCT_EXPORT_METHOD(listenCall:(RCTResponseSenderBlock)callback)
{
  callCallBackID = callback;
    
}


/*RCT_EXPORT_METHOD(login:(NSString *)username  password :(NSString *)password   domain:(NSString *)domain  callback:(RCTResponseSenderBlock)callback)
{
 
  
  NSLog( @"enter login ###############");
  
  [self loginTeste];
 
  NSLog( @"exited login ###############");
 
}*/

-(void)listenTick:(NSTimer *)timer {
  NSLog( @"linphone_core_iterate interate !!!!");
    linphone_core_iterate(lc);
    
}



RCT_EXPORT_METHOD(logout:(RCTResponseSenderBlock)callback)
{
  
  if(lc != NULL){
        LinphoneProxyConfig *proxy_cfg = linphone_core_create_proxy_config(lc);
        linphone_core_get_default_proxy(lc,&proxy_cfg); /* get default proxy config*/
        linphone_proxy_config_edit(proxy_cfg); /*start editing proxy configuration*/
        linphone_proxy_config_enable_register(proxy_cfg,FALSE); /*de-activate registration for this proxy config*/
        linphone_proxy_config_done(proxy_cfg); /*initiate REGISTER with expire = 0*/
        
        while(linphone_proxy_config_get_state(proxy_cfg) !=  LinphoneRegistrationCleared){
            linphone_core_iterate(lc); /*to make sure we receive call backs before shutting down*/
            ms_usleep(50000);
        }
        
        linphone_core_clear_all_auth_info(lc);
        linphone_core_clear_proxy_config(lc);
        linphone_core_destroy(lc);
        
        call = NULL;
        lc = NULL;
    }
  
  callback(@[[NSNull null], @"logout"]);

    
}


RCT_EXPORT_METHOD(call:(NSString *)address  displayName:(NSString *)displayName callback:(RCTResponseSenderBlock)callback)
{
  callCallBackID = callback;
  
    
    call = linphone_core_invite(lc, (char *)[address UTF8String]);
    linphone_call_ref(call);
    callback(@[[NSNull null], @"enter call"]);
}


RCT_EXPORT_METHOD(videocall:(RCTResponseSenderBlock)callback)
{
  callCallBackID = callback;
     callback(@[@"enter videocall"]);
}


RCT_EXPORT_METHOD(hangup:(RCTResponseSenderBlock)callback)
{
  callCallBackID = callback;
  
  // if(call && linphone_call_get_state(call) != LinphoneCallEnd){
    linphone_core_terminate_call(lc, call);
    // linphone_call_unref(call);
    //   }
    call = NULL;
  
    //callback(@[[NSNull null], @"enter videocall"]);
}

RCT_EXPORT_METHOD(toggleVideo:(RCTResponseSenderBlock)callback)
{    bool isenabled = FALSE;

  if (call != NULL && linphone_call_params_get_used_video_codec(linphone_call_get_current_params(call))) {
        if(isenabled){
            
        }else{
            
        }
    }
  
    callback(@[[NSNull null], @"enter toggleVideo"]);
}

RCT_EXPORT_METHOD(toggleSpeaker:(RCTResponseSenderBlock)callback)
{
  NSLog(@"Passou no speaker...");
    
    
    
    //  if (call != NULL && linphone_call_get_state(call) != LinphoneCallEnd){
    
    if (isspeaker) {
        UInt32 audioRouteOverride = kAudioSessionOverrideAudioRoute_Speaker;
        AudioSessionSetProperty(kAudioSessionProperty_OverrideAudioRoute, sizeof(audioRouteOverride),
                                &audioRouteOverride);
        
        NSLog(@"Ativou o speaker...");
        isspeaker = !isspeaker;
    } else {
        UInt32 audioRouteOverride = kAudioSessionOverrideAudioRoute_None;
        AudioSessionSetProperty(kAudioSessionProperty_OverrideAudioRoute, sizeof(audioRouteOverride),
                                &audioRouteOverride);
        NSLog(@"Desativou o speaker...");
        isspeaker = !isspeaker;
        
    }
    //}
    
  
    callback(@[[NSNull null], @"OK"]);
}


RCT_EXPORT_METHOD(toggleMute:(RCTResponseSenderBlock)callback)
{
  NSLog(@"Passou no toggleMute...");
    
  bool isenabled = FALSE;
    
    if(call && linphone_call_get_state(call) != LinphoneCallEnd){
        linphone_core_enable_mic(lc, isenabled);
    }
    
  
    callback(@[[NSNull null], @"OK"]);
}


RCT_EXPORT_METHOD(sendDtmf:(NSString*)dtmf callback:(RCTResponseSenderBlock)callback)
{
  NSLog(@"Passou no sendDtmf...");
    
    
    if(call && linphone_call_get_state(call) != LinphoneCallEnd){
        linphone_call_send_dtmf(call, [dtmf characterAtIndex:0]);
    }
  
    callback(@[[NSNull null], @"OK"]);
}


RCT_EXPORT_METHOD(getRemoteContact:(RCTResponseSenderBlock)callback)
{
  callCallBackID = callback;
  NSLog(@"Passou no getRemoteContact...");
    
    LinphoneCall *currentcall = linphone_core_get_current_call(lc);

    if (currentcall != NULL) {
        LinphoneAddress const * addr = linphone_call_get_remote_address(currentcall);

        if (addr != NULL) {
            RemoteAddress = [NSString stringWithUTF8String:linphone_address_get_username(addr)];
        }
    }
  
    callback(@[[NSNull null], RemoteAddress]);
}


RCT_EXPORT_METHOD(updateRegister:(RCTResponseSenderBlock)callback)
{
  callCallBackID = callback;
  NSLog(@"Passou no updateRegister...");
    
  linphone_core_refresh_registers(lc);
  
    callback(@[[NSNull null], @"OK"]);
}


RCT_EXPORT_METHOD(setLowBandwidth:(RCTResponseSenderBlock)callback)
{
  callCallBackID = callback;
  NSLog(@"Passou no setLowBandwidth...");
     /*
   LinphoneCall *currentcall = linphone_core_get_current_call(lc);
   LinphoneCallParams *currentcallparams = linphone_call_get_current_params(currentcall);
    
   linphone_call_params_enable_low_bandwidth(currentcallparams, true);*/
   
  
  
    callback(@[[NSNull null], @"OK"]);
}




RCT_EXPORT_METHOD(startStack)
{

  NSLog(@"Passou no startStack... startStack....");

}





///ghjghjghjghjghjghjghj

RCT_EXPORT_METHOD(login:(RCTResponseSenderBlock)callback)

{
  NSLog(@"Passou na MERDA!!!!");
  loginCallBackID = callback;
            
  
  NSString* username = @"902";
  NSString* password = @"4cd56bad0f15d47e01f5c58b706491f0";
  NSString* domain = @"voip.server.easyfront.live:9088";
  NSString* sip = [@"sip:" stringByAppendingString:[[username stringByAppendingString:@"@"] stringByAppendingString:domain]];
 
  char* identity = (char*)[sip UTF8String];
  
  NSLog( @"enter login aux ###############");
  
  
  
  
  
  if (lc == NULL) {
      LinphoneCoreVTable vtable = {0};
      
      signal(SIGINT,stop);
      /*
       Fill the LinphoneCoreVTable with application callbacks.
       All are optional. Here we only use the registration_state_changed callbacks
       in order to get notifications about the progress of the registration.
       */
      
      vtable.registration_state_changed = registration_state_changed;
      
      /*
       Fill the LinphoneCoreVTable with application callbacks.
       All are optional. Here we only use the call_state_changed callbacks
       in order to get notifications about the progress of the call.
       */
      vtable.call_state_changed = call_state_changed;
      
      lc = linphone_core_new(&vtable, NULL, NULL, NULL);
  }
  
 //linphone_core_set_dns_servers(lc, bctbx_list_t);
  
  
  LinphoneProxyConfig *proxy_cfg = linphone_core_create_proxy_config(lc);
  LinphoneAddress *from = linphone_address_new(identity);
  
  /*create authentication structure from identity*/
  LinphoneAuthInfo *info=linphone_auth_info_new(linphone_address_get_username(from),NULL,(char*)[password UTF8String],NULL,(char*)[domain UTF8String],(char*)[domain UTF8String]);
  linphone_auth_info_set_algorithm(info,(char*)[@"md5" UTF8String]);
  linphone_core_add_auth_info(lc,info); /*add authentication info to LinphoneCore*/
  
  // configure proxy entries
LinphoneAddress *address = linphone_core_interpret_url(lc, identity);
//linphone_address_set_port(address,9060);
linphone_proxy_config_set_identity_address(proxy_cfg,address); /*set identity with user name and domain*/
  const char* server_addr = (char*)[domain UTF8String]; /*extract domain address from identity*/
  linphone_proxy_config_set_server_addr(proxy_cfg,server_addr); /* we assume domain = proxy server address*/
  linphone_proxy_config_enable_register(proxy_cfg,TRUE); /*activate registration for this proxy config*/
  linphone_address_destroy(from); /*release resource*/
  linphone_core_add_proxy_config(lc,proxy_cfg); /*add proxy config to linphone core*/
linphone_core_set_default_proxy_config(lc,proxy_cfg); /*set to default proxy*/
  
  LCSipTransports transport;
  linphone_core_get_sip_transports(lc, &transport);
  transport.tls_port = 0;
  transport.tcp_port = LC_SIP_TRANSPORT_RANDOM;
  transport.udp_port = LC_SIP_TRANSPORT_RANDOM;
  
  linphone_core_set_sip_transports(lc, &transport);
  
  /* main loop for receiving notifications and doing background linphonecore work: */
  
  //while(running){
  //    linphone_core_iterate(lc); /* first iterate initiates registration */
  //    ms_usleep(50000);
  //}
  call = NULL;
  running = TRUE;
  tListen = [NSTimer scheduledTimerWithTimeInterval: 0.05
                                             target: self
                                           selector:@selector(listenTick:)
                                           userInfo: nil repeats:YES];
  





  
   
}




@end
