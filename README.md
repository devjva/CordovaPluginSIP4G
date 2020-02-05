Based on https://github.com/sezerkorkmaz/cordova-plugin-sip

(Now working on mobile network)


```

 var sipManager = {
        register: function () {
            cordova.plugins.sip.login('Extension', 'Password', 'IP Address:Port', function (e) {

                if (e == 'RegistrationSuccess') {
                    console.log(e);
                    sipManager.listen();
                } else {
                    alert("Registration Failed!");
                }

            }, function (e) { console.log(e) })
        },
        call: function () {
            cordova.plugins.sip.call('sip:111@192.168.1.111:5060', '203', sipManager.events, sipManager.events)
        },
        listen: function () {
            cordova.plugins.sip.listenCall(sipManager.events, sipManager.events);
        },
        hangup: function () {
            cordova.plugins.sip.hangup(function (e) { console.log(e) }, function (e) { console.log(e) })
        },
		    updateRegister: function () {			
            cordova.plugins.sip.updateRegister(function (e) { console.log(e) }, function (e) { console.log(e) })
        },
        events: function (e) {
            console.log(e);
            if (e == 'Incoming') {
                var r = confirm("Incoming Call");
                if (r == true) {
                    cordova.plugins.sip.accept(true, sipManager.events, sipManager.events);
                } else {

                }
            }
            if (e == 'Connected') {
                alert("Connected!");
                sipManager.listen();
            }
            if (e == 'Error') {
                alert("Call Error!");
                sipManager.listen();
            }
            if (e == 'End') {
                alert("Call End!");
                sipManager.listen();
            }


        }
    }
´´´
