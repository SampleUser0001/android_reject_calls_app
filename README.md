# Android 着信拒否アプリ

## 概要

このアプリは、指定した電話番号を着信拒否するAndroidアプリです。電話番号は完全一致だけでなく、前方一致の電話番号も指定できます。

### 前方一致の例

| 指定した番号 | 電話番号の例   | 拒否するか |
| :----------- | :------------- | :--------- |
| 0800         | 0800-111-1111  | 拒否する   |
| 0800         | 0800-211-1111  | 拒否する   |
| 0800         | 080-1211-1111  | 拒否しない |

## 実装した主な機能

1. **電話番号の登録・管理**
   - ユーザーは拒否したい電話番号を入力して追加できます
   - 追加された番号は一覧表示されます
   - 長押しで番号を削除できます

2. **着信拒否機能**
   - 登録された番号と前方一致する着信を自動的に拒否します
   - SharedPreferencesを使用して拒否リストを保存します

3. **複数のAndroidバージョンに対応**
   - Android 9.0以降：CallScreeningServiceを使用
   - Android 9.0未満：BroadcastReceiverとリフレクションを使用

## プロジェクト構造

```
android_reject_calls_app/
├── app/
│   └── build.gradle        # アプリモジュールのビルド設定
├── src/
│   └── main/
│       ├── AndroidManifest.xml  # アプリの基本設定と権限
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── rejectcalls/
│       │               ├── MainActivity.java          # メイン画面の実装
│       │               ├── CallReceiver.java          # 着信検出用BroadcastReceiver
│       │               └── CallScreeningService.java  # 着信スクリーニングサービス
│       └── res/
│           ├── layout/
│           │   └── activity_main.xml  # メイン画面のレイアウト
│           └── values/
│               ├── strings.xml  # 文字列リソース
│               └── styles.xml   # スタイル設定
├── build.gradle        # プロジェクト全体のビルド設定
└── settings.gradle     # プロジェクト設定
```

## 各ファイルの説明

### AndroidManifest.xml

アプリの基本設定と必要な権限を定義しています。主な権限として：

- `READ_PHONE_STATE` - 電話の状態を監視するための権限
- `CALL_PHONE` - 電話の操作に関する権限
- `READ_CALL_LOG` - 通話履歴の読み取り権限
- `ANSWER_PHONE_CALLS` - 着信応答の権限
- `MODIFY_PHONE_STATE` - 電話の状態を変更する権限
- `RECEIVE_BOOT_COMPLETED` - 端末起動時の通知を受け取る権限

また、`CallReceiver`と`CallScreeningService`を登録しています。

### MainActivity.java

アプリのメイン画面を実装しています。主な機能：

- 拒否する電話番号の入力と追加
- 拒否リストの表示
- 長押しによる番号の削除
- 必要な権限のチェックとリクエスト

### CallReceiver.java

着信を検出して拒否するためのBroadcastReceiverです。主な機能：

- 着信イベントの検出
- 拒否リストとの照合（前方一致）
- Android 9.0未満での着信拒否の実装（リフレクション使用）

### CallScreeningService.java

Android 9.0以降で使用する着信スクリーニングサービスです。主な機能：

- 着信のスクリーニング
- 拒否リストとの照合（前方一致）
- 着信の許可または拒否の決定

### activity_main.xml

メイン画面のレイアウトを定義しています。主な要素：

- 電話番号入力フィールド
- 追加ボタン
- 拒否リストを表示するListView
- 操作説明のテキスト

### strings.xml

アプリ内で使用する文字列リソースを定義しています：

- アプリ名
- ボタンのラベル
- メッセージテキスト
- エラーメッセージ

### styles.xml

アプリのテーマとスタイルを定義しています：

- 基本テーマ（AppCompat）
- カラースキーム（緑系）

### build.gradle（プロジェクトレベル）

プロジェクト全体のビルド設定を定義しています：

- Gradleバージョン
- リポジトリ設定

### app/build.gradle（アプリモジュールレベル）

アプリモジュールのビルド設定を定義しています：

- コンパイルSDKバージョン（33）
- 最小SDKバージョン（21）
- ターゲットSDKバージョン（33）
- 依存ライブラリ

### settings.gradle

プロジェクト設定を定義しています：

- プロジェクト名
- 含まれるモジュール

## 使用方法

1. アプリを起動すると、必要な権限のリクエストが表示されます
2. 拒否したい電話番号（市外局番や事業者識別番号など）を入力して「番号を追加」ボタンをタップ
3. 追加された番号は画面に一覧表示されます
4. 番号を削除したい場合は、リスト内の番号を長押しします
5. 登録した番号で始まる電話番号からの着信は自動的に拒否されます

## 注意事項

- Android 9.0以降では、コールスクリーニングサービスの設定画面への誘導が行われます
- 一部の端末では、メーカー独自の制限により着信拒否機能が動作しない場合があります
- このアプリは、Android Studioでビルドして実行する必要があります

## Ubuntu環境でのAndroid Studioセットアップと実行手順

### 1. Android Studioのインストール（まだインストールしていない場合）

1. ターミナルを開き、以下のコマンドを実行してJDKをインストールします：
   ```bash
   sudo apt update
   sudo apt install openjdk-11-jdk
   ```

2. Android Studioの公式サイトからLinux版をダウンロードします：
   https://developer.android.com/studio

3. ダウンロードしたzipファイルを展開します：
   ```bash
   cd ~/Downloads
   tar -xvf android-studio-*.tar.gz -C ~/
   ```

4. Android Studioを起動します：
   ```bash
   cd ~/android-studio/bin
   ./studio.sh
   ```

5. セットアップウィザードに従って初期設定を完了します

### 2. プロジェクトのインポート

1. Android Studioを起動します
2. メニューから「File」→「New」→「Import Project」を選択します
3. このプロジェクトのルートディレクトリ（`/home/ubuntuuser/environment/android_reject_calls_app`）を選択して「OK」をクリックします
4. Gradleプロジェクトとして認識されるので、デフォルト設定のまま「Next」→「Finish」をクリックします
5. Gradleの同期が完了するまで待ちます（右下にプログレスバーが表示されます）

### 3. 必要なSDKのインストール

1. プロジェクトのインポート後、足りないSDKコンポーネントがある場合は通知が表示されます
2. 通知が表示された場合は「Install missing platforms and sync project」をクリックします
3. Android SDK Managerが開くので、必要なコンポーネントを選択して「OK」をクリックします
   - Android SDK Platform 33（またはそれ以上）
   - Android SDK Build-Tools
   - Android SDK Platform-Tools
4. ライセンス契約に同意して、インストールを完了します
5. インストール完了後、再度Gradleの同期が行われるので完了するまで待ちます

### 4. プロジェクト構造の確認

1. プロジェクトビューを「Android」に切り替えます（左上のドロップダウンメニューから選択）
2. 以下のファイルが正しく配置されていることを確認します：
   - `app/src/main/AndroidManifest.xml`
   - `app/src/main/java/com/example/rejectcalls/MainActivity.java`
   - `app/src/main/java/com/example/rejectcalls/CallReceiver.java`
   - `app/src/main/java/com/example/rejectcalls/CallScreeningService.java`
   - `app/src/main/res/layout/activity_main.xml`
   - `app/src/main/res/values/strings.xml`
   - `app/src/main/res/values/styles.xml`
3. ファイルの配置が異なる場合は、正しいディレクトリに移動させます
   - Ubuntuでは、ファイルの移動はドラッグ＆ドロップまたは右クリックメニューから行えます

### 5. ビルド設定の確認

1. `build.gradle`（プロジェクトレベル）と`app/build.gradle`（アプリモジュールレベル）の内容を確認します
2. 特に以下の点を確認します：
   - コンパイルSDKバージョン（33以上推奨）
   - 最小SDKバージョン（21以上推奨）
   - 依存ライブラリのバージョン
3. 必要に応じて、最新のライブラリバージョンに更新します

### 6. KVMの設定（エミュレータの高速化のため）

1. KVM（Kernel-based Virtual Machine）がインストールされているか確認します：
   ```bash
   kvm-ok
   ```

2. インストールされていない場合は、以下のコマンドでインストールします：
   ```bash
   sudo apt install qemu-kvm libvirt-daemon-system libvirt-clients bridge-utils
   ```

3. ユーザーをkvm、libvirtグループに追加します：
   ```bash
   sudo adduser $USER kvm
   sudo adduser $USER libvirt
   ```

4. 変更を適用するためにログアウトして再ログインします

### 7. エミュレータのセットアップと実行

1. エミュレータのセットアップ：
   - メニューから「Tools」→「Device Manager」（または「AVD Manager」）を選択します
   - 「Create Virtual Device」をクリックします
   - 端末を選択（例：Pixel 6）して「Next」をクリックします
   - システムイメージを選択（API 33以上推奨）して「Next」→「Finish」をクリックします
   - 「Graphics」設定で「Hardware - GLES 2.0」を選択すると、Ubuntuでのパフォーマンスが向上します

2. アプリのビルドと実行：
   - ツールバーの緑色の再生ボタン（▶）をクリックします
   - 起動するデバイスを選択（先ほど作成したエミュレータ）して「OK」をクリックします
   - エミュレータが起動し、アプリがインストールされて実行されます

### 8. 実機でのテスト（Ubuntu環境）

1. Ubuntuで実機を認識させるための設定：
   - ターミナルで以下のコマンドを実行して、udevルールを作成します：
     ```bash
     sudo apt install android-tools-adb
     cd /etc/udev/rules.d/
     sudo touch 51-android.rules
     sudo chmod a+r 51-android.rules
     ```

   - エディタで51-android.rulesを開き、以下の内容を追加します（VENDORIDは端末によって異なります）：
     ```
     SUBSYSTEM=="usb", ATTR{idVendor}=="VENDORID", MODE="0666", GROUP="plugdev"
     ```
     （VENDORIDは、`lsusb`コマンドで確認できます。例：Googleデバイスは「18d1」）

   - udevルールを再読み込みします：
     ```bash
     sudo udevadm control --reload-rules
     sudo udevadm trigger
     ```

2. 実機での実行：
   - 端末のUSBデバッグを有効にします（設定→開発者オプション→USBデバッグ）
   - USBケーブルで端末をPCに接続します
   - ターミナルで`adb devices`を実行して、デバイスが認識されていることを確認します
   - Android Studioのデバイス選択ドロップダウンから実機を選択します
   - 緑色の再生ボタン（▶）をクリックしてアプリをインストールします

### 9. デバッグとトラブルシューティング（Ubuntu固有の問題）

1. エミュレータが起動しない場合：
   - KVMが正しく設定されているか確認します：`kvm-ok`
   - グラフィックドライバを更新します：`sudo ubuntu-drivers autoinstall`
   - エミュレータの設定で「Graphics」を「Software - GLES 2.0」に変更します

2. ADBが実機を認識しない場合：
   - 別のUSBポートを試します
   - 別のUSBケーブルを試します
   - `adb kill-server`を実行してから`adb start-server`を実行します
   - 端末の「USBの設定」で「ファイル転送」モードを選択します

3. Logcatの確認：
   - Android Studioの下部にある「Logcat」タブを開きます
   - フィルターに「CallReceiver」または「CallScreeningService」と入力します
   - 着信時のログメッセージを確認して、アプリが正しく動作しているか確認します
