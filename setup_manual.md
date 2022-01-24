# セットアップ方法について

以下のコマンドでホームディレクトリに移動します．

```sh
$ cd
```

この後`cd`コマンドで任意のディレクトリに移動

リポジトリのクローン

```sh
$ git clone https://github.com/e1b19099/BlackJack.git
```

リポジトリが正しくクローン出来たかどうかを確認するために `ls` コマンドを実行してリポジトリに`BlackJack`があるか確認しましょう
．
```sh
$ ls
```

`gradlew` を `bash` を利用して実行します．シェルスクリプトで記述されているので以下のように実行します．

```sh
$ bash ./gradlew
```

確認出来たら `cd` コマンドを利用してリポジトリに移動しましょう．以下の `BlackJack` はリポジトリ名です．

```sh
$ cd BlackJack
```

移動ができれば，リポジトリの内容を `ls` コマンドを利用して確認しましょう．

```sh
$ ls -al
```

Linuxでは**1024番ポート**以下のポートは管理者権限を持つアプリケーションのみが実行できるように設定されています。

以下の`setcap`コマンドを利用して，`java` コマンドに1024番ポート以下のポートが利用できる設定を加えることで本アプリケーションをサーバ上で動かすことが可能になります。

```sh
$ sudo setcap CAP_NET_BIND_SERVICE+ep /usr/lib/jvm/java-11-amazon-corretto/bin/java
```


`getcap`コマンドで設定されているか確認してください

```sh
$ sudo getcap /usr/lib/jvm/java-11-amazon-corretto/bin/java
/usr/lib/jvm/java-11-amazon-corretto/bin/java = cap_net_bind_service+ep
```

以下のコマンドでアプリケーションを起動できます。

```sh
$ bash ./gradlew bootrun
```
