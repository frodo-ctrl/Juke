# Debug Session: tvm-network-retry
- **Status**: [OPEN]
- **progress_status**: blocked (user-only Trakt app credential creation required)
- **Issue**: Original VALIDATED/network-gate failure is fixed; app now reaches upstream APIs but Discover is blocked by Trakt HTTP 403 because its client ID is still a placeholder.
- **Evidence Source**: Android logcat, adb device state, host/emulator network probes, post-fix screenshot
- **Evidence Directory**: C:\Users\90594\AppData\Local\Temp\trae-debug\Juke\tvm-network-retry
- **Debug Server**: Not started; existing logcat sink was sufficient, no code instrumentation required
- **Log File**: Not created (existing logcat used)
- **Env File**: Not created

## Reproduction Steps
1. Host can reach api.themoviedb.org through the Singapore VPN node.
2. Restart the app on emulator-5554.
3. Discover screen shows Network error and Android reports No Internet Connection.

## Hypotheses & Verification
| ID | Hypothesis | Likelihood | Effort | Evidence |
|----|------------|------------|--------|----------|
| A | Emulator traffic is not entering the VPN tunnel. | Med | Low | REJECTED: after validation, real Trakt/TMDB requests and responses appear in logcat |
| B | Emulator DNS or cached route to api.themoviedb.org is broken. | Med | Low | CONFIRMED for Android's Google validation probes; network 101 lacked VALIDATED and probes failed |
| C | TMDB_API_KEY is missing, not packaged, or rejected. | Med | Low | REJECTED: host request with configured key returned HTTP 200 |
| D | TLS or connection timeout still affects this VPN route. | Med | Low | REJECTED: post-fix requests reached upstream services |
| E | The app maps a non-network failure to the same generic error. | Low | Med | REJECTED for original failure: InternetConnectionPlugin explicitly throws before requests when VALIDATED is absent |
| F | Trakt credentials are missing and independently block the catalogue. | High | Low | CONFIRMED: HTTP 403 Missing API key from api.trakt.tv; local.properties still has placeholders |

## Log Evidence
- Pre-fix: TrendingShowsInteractor/FeaturedShowsInteractor threw ApiNetworkException: No internet connection.
- PlatformInternetConnectionChecker requires INTERNET + VALIDATED; InternetConnectionPlugin throws NoInternetException before the request.
- Pre-fix network 101 had INTERNET but no VALIDATED; NetworkMonitor Google generate_204 probes failed.
- Fix: captive portal URLs set to connect.rom.miui.com/generate_204 and emulator Wi-Fi reconnected.
- Post-fix network 103 has INTERNET + VALIDATED and NetworkMonitor receives 204.
- Post-fix app performs real API calls; Trakt returns HTTP 403 Missing API key and UI changes from Network error to Access forbidden.
- Post-fix screenshot: C:\Users\90594\AppData\Local\Temp\trae-debug\Juke\tvm-network-retry\tvm-network-retry-post.png

## Verification Conclusion
The original network-gate problem is verified fixed. Catalog loading remains blocked by a separate configuration issue: Trakt Client ID/Secret must be created by the user and packaged through a rebuild.

## Final Evidence Gate
- **Evidence Sources**: adb logcat, dumpsys connectivity, curl HTTP status probes, post-fix screenshot
- **progress_status**: blocked (user-only Trakt credential creation)
- **Hypothesis Conclusions**: A=REJECTED; B=CONFIRMED (validation path); C=REJECTED; D=REJECTED; E=REJECTED; F=CONFIRMED
