import 'package:adc_handson_session/login/application/logout.dart';
import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:adc_handson_session/login/application/auth.dart';
import 'package:shared_preferences/shared_preferences.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  late Future<Position> userPosition;

  @override
  void initState() {
    super.initState();
    //userPosition = getCurrentLocation();
  }

  Future<Position> getCurrentLocation() async {
    // TODO: It's important to check:
    // - if location services are enabled
    // - if the user has given permission to use his/her location
    // Check for an example: https://pub.dev/packages/geolocator
    var p = await Geolocator.getCurrentPosition(
      desiredAccuracy: LocationAccuracy.high,
      forceAndroidLocationManager: true,
    );

    return p;
  }

  void logoutButtonPressed() async {
    if (await Logout.logoutUser()) {
      Navigator.pop(context);
    } else {
      // Wrong credentials
      showDialog(
        context: context,
        builder: (context) {
          return const AlertDialog(
            content: Text(
                "Wrong token id! Critical error! Contact the support team."),
          );
        },
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Our First App - Main Screen'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center, // Change to center
          children: <Widget>[
            ElevatedButton(
              onPressed: () {
                // TODO: Implement button functionality
              },
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.all(20),
                textStyle: const TextStyle(fontSize: 20),
              ),
              child: const Text('Change users role'),
            ),
            const SizedBox(height: 10), // Add SizedBox with desired height
            ElevatedButton(
              onPressed: () {
                // TODO: Implement button functionality
              },
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.all(20),
                textStyle: const TextStyle(fontSize: 20),
              ),
              child: const Text('Change users sate'),
            ),
            const SizedBox(height: 10), // Add SizedBox with desired height
            ElevatedButton(
              onPressed: () {
                // TODO: Implement button functionality
              },
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.all(20),
                textStyle: const TextStyle(fontSize: 20),
              ),
              child: const Text('Remove users'),
            ),
            const SizedBox(height: 10), // Add SizedBox with desired height
            ElevatedButton(
              onPressed: () {
                // TODO: Implement button functionality
              },
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.all(20),
                textStyle: const TextStyle(fontSize: 20),
              ),
              child: const Text('Edit users attributes'),
            ),
            const SizedBox(height: 10), // Add SizedBox with desired height
            ElevatedButton(
              onPressed: () {
                // TODO: Implement button functionality
              },
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.all(20),
                textStyle: const TextStyle(fontSize: 20),
              ),
              child: const Text('Edit password'),
            ),
            const SizedBox(height: 10), // Add SizedBox with desired height
            ElevatedButton(
              onPressed: () {
                // TODO: Implement button functionality
              },
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.all(20),
                textStyle: const TextStyle(fontSize: 20),
              ),
              child: const Text('View token, role and state'),
            ),
            const SizedBox(height: 10), // Add SizedBox with desired height
            ElevatedButton(
              onPressed: () {
                logoutButtonPressed();
              },
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.all(20),
                textStyle: const TextStyle(fontSize: 20),
                backgroundColor: Colors.red, // Set the background color to red
              ),
              child: const Text(
                'Logout',
                style: TextStyle(
                    color: Colors.white), // Set the text color to white
              ),
            ),
          ],
        ),
      ),
    );
  }
}
