# ソースコード解析
## 概要
プログラミングコンテストに提出されたソースコードを解析するためのプログラムです．

## 導入
* このプロジェクトを導入
* antlrをインストールし，ライブラリとしてプロジェクトに導入
* [Antlr grammar list](https://github.com/antlr/grammars-v4) から文法ファイルの取得
* /lexer/cpp等にLexerを作成し，そのソースをこのプロジェクトにリンク
* リンクしたLexerのソースファイルにpackage文の追加

## 現在のプロジェクトの構造
* Analyzer.java
* Lexer.java
* lexer/cpp/CPP用文法解析コード(antlrより生成)

## 利用方法
現在の機能は，C++のトークン分割，キーワード集計のみです．
parser機能を用いた処理を追加中．

1. /dataにtest.cppを置く  
<!--
2. test.cppから#define以外のプリプロセッサ命令を取り除く
3. gcc -E test.cpp により，#define展開
-->  
2. このプログラムを動作させる

## オプションと機能
* -l lexer機能