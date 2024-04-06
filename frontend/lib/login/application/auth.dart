import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:email_validator/email_validator.dart';
import 'package:shared_preferences/shared_preferences.dart';

class Authentication {
  static Future<void> saveResponse(String response) async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.setString('response', response);
  }

  static Future<String> getResponse() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    return prefs.getString('response') ?? '';
  }

  static void saveToken(dynamic token) async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.setString('token', token);
  }

  static Future<dynamic> getToken() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    return prefs.getString('token') ?? '';
  }

  static void saveTokenInfo(String username, String tokenID, int creationData,
      int expirationData) async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.setString('username', username);
    prefs.setString('tokenID', tokenID);
    prefs.setInt('creationData', creationData);
    prefs.setInt('expirationData', expirationData);
  }

  static Future<String> getTokenUsername() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    return prefs.getString('username') ?? '';
  }

  static Future<String> getTokenId() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    return prefs.getString('tokenID') ?? '';
  }

  static Future<int> getTokenCreationData() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    return prefs.getInt('creationData') ?? 0;
  }

  static Future<int> getTokenExpirationData() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    return prefs.getInt('expirationData') ?? 0;
  }

  static bool isEmailCompliant(String email) {
    return EmailValidator.validate(email);
  }

  static bool isPasswordCompliant(String password, [int minLength = 6]) {
    //Null-safety ensures that password is never null
    if (password.isEmpty) {
      return false;
    }

    bool hasUppercase = password.contains(RegExp(r'[A-Z]'));
    bool hasDigits = password.contains(RegExp(r'[0-9]'));
    bool hasLowercase = password.contains(RegExp(r'[a-z]'));
    bool hasSpecialCharacters =
        password.contains(RegExp(r'[!@#$%^&*(),.?":{}|<>]'));
    bool hasMinLength = password.length > minLength;

    return hasDigits &
        hasUppercase &
        hasLowercase &
        //hasSpecialCharacters &
        hasMinLength;
  }

  static Future<bool> loginUser(String username, String password) async {
    //  API Call to authenticate an user (GoogleAppEngine endpoint)

    // Note: hash passwords before sending them through the communication channel
    // Example: https://pub.dev/packages/hash_password

    // In the meanwhile, if you don't have an endpoint to authenticate users in
    // Google app Engine, send a POST to https://dummyjson.com/docs/auth.
    // Body should be a json {'username': <username>, 'password': <password>}
    // Use username: hbingley1 - password: CQutx25i8r
    // More info: https://dummyjson.com/docs/auth
    final result = await fetchAuthenticate(username, password);
    return result;
  }

  static Future<bool> fetchAuthenticate(
      String username, String password) async {
    final response = await http.post(
      Uri.parse("https://consummate-link-415914.oa.r.appspot.com/rest/login/"),
      headers: <String, String>{
        "Content-Type": "application/json",
      },
      body: jsonEncode(<String, String>{
        "username": username,
        "password": password,
      }),
    );

    if (response.statusCode == 200) {
      // If the server did return a 200 OK response,
      // then parse the JSON.
      print(jsonDecode(response.body));
      saveToken(response.body);
      Map<String, dynamic> jsonData = jsonDecode(response.body);
      saveTokenInfo(jsonData["username"], jsonData["tokenID"],
          jsonData['creationData'], jsonData['expirationData']);
      final resposta = await http.post(
        Uri.parse(
            "https://consummate-link-415914.oa.r.appspot.com/rest/list/users"),
        headers: <String, String>{
          "Content-Type": "application/json",
        },
        body: jsonEncode(<dynamic, dynamic>{
          "token": {
            "username": await getTokenUsername(),
            "tokenID": await getTokenId(),
            "creationData": await getTokenCreationData(),
            "expirationData": await getTokenExpirationData(),
          },
        }),
      );
      print(resposta.body);
      return true;
    } else {
      return false;
    }
  }
}
