# automaton.java

NFA,DFAの図作成と経路表示をするためのgraphizのdotソースを出力します

## 入力フォーマット

```text
使う文字の種類 文字の列挙(スペース区切り) 空文字はEPS
状態数 遷移数
頂点名 受理状態かどうか 開始地点かどうか
︙
遷移元の状態名 文字 遷移先の状態名
︙
処理させる文字列
```
出力される前半は状態遷移図
後半は、文字列を機械が処理したときの経路です。

## 入力例

DFAサンプル

```
2 0 1
6 12
a false true
b false false
c false false
d false false
e false false
f true false
a 1 b
a 0 c
b 1 f
b 0 e
e 1 f
e 0 f
f 1 f
f 0 f
c 1 d
c 0 f
d 1 f
d 0 f
101000
```

NFAサンプル

```
3 0 1 EPS
4 9
1 false true
2 false false
3 false false
4 true false
1 0 1
1 1 1
1 1 2
2 0 3
2 1 3
2 EPS 3
3 0 4
3 1 4
3 EPS 4
000100
```

初期状態に空文字があり複数状態からはじまるNFA

```
3 0 1 EPS
12 16
a false true
b false false
c false false
d false false
e false false
f false false
g false false
h false false
i false false
j false false
k false false
l true false
a EPS b
a EPS c
a 1 f
a 0 g
b EPS d
c EPS e
d 1 h
d 0 i
e 0 j
e 1 k
h 1 l
i 1 l
f 1 l
g 1 l
j 1 l
k 1 l
1111
```

