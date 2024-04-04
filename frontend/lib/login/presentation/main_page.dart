import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:adc_handson_session/login/application/auth.dart';

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
    userPosition = getCurrentLocation();
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text('Our First App - Main Screen'),
        ),
        body: Center(
            child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Container(
              padding: const EdgeInsets.fromLTRB(20, 20, 20, 70),
              child: const FlutterLogo(
                size: 40,
              ),
            ),
            Column(
              children: [
                const Text('Welcome to the best App ever!'),
                const Padding(padding: EdgeInsets.fromLTRB(20, 20, 20, 0)),
                // FutureBuilder:
                FutureBuilder<Position>(
                  future: userPosition,
                  builder: (context, snapshot) {
                    if (snapshot.hasData) {
                      return Text(
                          "User Latitude: ${snapshot.data!.latitude} - User Longitude: ${snapshot.data!.longitude}");
                    } else if (snapshot.hasError) {
                      return Text('${snapshot.error}');
                    }

                    // By default, show a loading spinner.
                    return const CircularProgressIndicator();
                  },
                ),
              ],
            ),
          ],
        )));
  }
}
