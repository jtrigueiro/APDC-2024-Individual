import 'dart:convert';
import 'package:adc_handson_session/login/application/auth.dart';
import 'package:http/http.dart' as http;
import 'package:email_validator/email_validator.dart';

class ChangeState {
  static Future<bool> changeUserState(String targetUsername) async {
    final result = await fetchState(targetUsername);
    return result;
  }

  static Future<bool> fetchState(String targetUsername) async {
    final response = await http.put(
      Uri.parse(
          "https://consummate-link-415914.oa.r.appspot.com/rest/changepersmissions/state"),
      headers: <String, String>{
        "Content-Type": "application/json",
      },
      body: jsonEncode(<dynamic, dynamic>{
        "targetUsername": targetUsername,
        "token": {
          "username": await Authentication.getTokenUsername(),
          "tokenID": await Authentication.getTokenId(),
          "creationData": await Authentication.getTokenCreationData(),
          "expirationData": await Authentication.getTokenExpirationData(),
        },
      }),
    );
    await Authentication.saveResponse(response.body.toString());
    if (response.statusCode == 200) {
      // If the server did return a 200 OK response,
      return true;
    } else {
      return false;
    }
  }
}
