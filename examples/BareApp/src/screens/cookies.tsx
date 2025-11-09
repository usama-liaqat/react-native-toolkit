import { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  Button,
  ScrollView,
  StyleSheet,
  Alert,
} from 'react-native';
// import WebView from 'react-native-webview';
import type { Cookies } from '@react-native-toolkit/cookies'; // replace with actual import
import { CookieManager } from '@react-native-toolkit/cookies'; // replace with actual import

export default function CookiesTestScreen() {
  const [url, setUrl] = useState('https://example.com');
  const [name, setName] = useState('');
  const [value, setValue] = useState('');
  const [cookies, setCookies] = useState<Cookies>({});
  const [webUrl, setWebUrl] = useState('https://example.com');

  const handleSetCookie = async () => {
    try {
      const cookie = { name, value, path: '/', domain: new URL(url).hostname };
      await CookieManager.set(url, cookie, true);
      Alert.alert('Success', `Cookie ${name} set!`);
    } catch (err) {
      Alert.alert('Error', String(err));
    }
  };

  const handleGetCookies = async () => {
    try {
      const data = await CookieManager.get(url, true);
      setCookies(data);
    } catch (err) {
      Alert.alert('Error', String(err));
    }
  };

  const handleClearAll = async () => {
    try {
      await CookieManager.clearAll(true);
      setCookies({});
      Alert.alert('Cleared', 'All cookies removed.');
    } catch (err) {
      Alert.alert('Error', String(err));
    }
  };

  const handleRemoveByName = async () => {
    try {
      await CookieManager.clearByName(url, name, true);
      Alert.alert('Removed', `Cookie "${name}" removed.`);
      handleGetCookies();
    } catch (err) {
      Alert.alert('Error', String(err));
    }
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>🍪 Cookie Tester</Text>

      <TextInput
        style={styles.input}
        placeholder="Enter URL"
        value={url}
        onChangeText={setUrl}
      />
      <TextInput
        style={styles.input}
        placeholder="Cookie name"
        value={name}
        onChangeText={setName}
      />
      <TextInput
        style={styles.input}
        placeholder="Cookie value"
        value={value}
        onChangeText={setValue}
      />

      <View style={styles.buttonRow}>
        <Button title="Set Cookie" onPress={handleSetCookie} />
        <Button title="Get Cookies" onPress={handleGetCookies} />
      </View>
      <View style={styles.buttonRow}>
        <Button title="Remove by Name" onPress={handleRemoveByName} />
        <Button title="Clear All" onPress={handleClearAll} />
      </View>

      <Text style={styles.subtitle}>Cookies:</Text>
      <Text style={styles.cookieBox}>
        {Object.keys(cookies).length === 0
          ? 'No cookies'
          : JSON.stringify(cookies, null, 2)}
      </Text>

      <Text style={styles.subtitle}>Test in WebView</Text>
      <TextInput
        style={styles.input}
        placeholder="Enter WebView URL"
        value={webUrl}
        onChangeText={setWebUrl}
      />
      <View style={styles.webViewBox}>
        {/*<WebView source={{ uri: webUrl }} />*/}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 22,
    fontWeight: '700',
    marginBottom: 16,
  },
  subtitle: {
    fontSize: 16,
    fontWeight: '600',
    marginTop: 16,
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 8,
    marginVertical: 6,
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginVertical: 6,
  },
  cookieBox: {
    backgroundColor: '#f7f7f7',
    borderRadius: 8,
    padding: 10,
    fontFamily: 'Courier',
  },
  webViewBox: {
    height: 300,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    overflow: 'hidden',
    marginVertical: 10,
  },
});
