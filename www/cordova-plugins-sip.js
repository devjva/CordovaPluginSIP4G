cordova.define("cordova-plugin-sip.linphone", function(require, exports, module) {
module.exports =
{
    login: function (username, password, domain, transport, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            "Linphone",
            "login",
            [username, password, domain, transport]
        );
    },
    logout: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            "Linphone",
            "logout",
            []
        );
    },
    accept: function (value, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            "Linphone",
            "acceptCall",
            [value]
        );
    },
    listenCall: function (successCallback, errorCallback) {
        cordova.exec(
                successCallback,
                errorCallback,
                "Linphone",
                "listenCall",
                []
            );
    },
    call: function (address, displayName, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            "Linphone",
            "call",
            [address, displayName]
        );
    },
    videocall: function (address, displayName, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            "Linphone",
            "videocall",
            [address, displayName]
        );
    },
    hangup: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            "Linphone",
            "hangup",
            []
        );
    },
    toggleVideo: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            "Linphone",
            "toggleVideo",
            []
        );
    },
    toggleSpeaker: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            "Linphone",
            "toggleSpeaker",
            []
        );
    },
    toggleMute: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            "Linphone",
            "toggleMute",
            []
        );
    },
    sendDtmf: function (number, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            "Linphone",
            "sendDtmf",
            [number]
        );
    },
    updateRegister: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            "Linphone",
            "updateRegister",
            []
        );
    },
    getRemoteContact: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            "Linphone",
            "getRemoteContact",
            []
        );
    }
};

});
