# Shell

## 什么是Shell

Shell是一个命令行解释器，它接收应用程序/用户命令，然后调用操作系统内核

Shell 是一个用 C 语言编写的程序，它是用户使用 Linux 的桥梁

Shell还是一个功能相当强大的编程语言，易编写、易调试、灵活性强。

Shell 编程跟 java、php 编程一样，只要有一个能编写代码的文本编辑器和一个能解释执行的脚本解释器就可以了

##  入门

Linux 的 Shell 种类众多，常见的有：cat /etc/shells

![](C:\Users\DELL\Desktop\Linux\img\shells.jpg)

 Bash由于易用和免费，Bash 在日常工作中被广泛使用。同时，Bash 也是大多数 Linux 系统默认的 Shell。

echo $SHELL 

输出默认shell

脚本以#!/bin/bash 开头（指定解析器）

 **案例**   

  vim helloworld.sh 在 helloworld.sh 中输入如下内容

```
 #!/bin/bash
 echo "helloworld" 
```

"#!" 是一个约定的标记，它告诉系统这个脚本需要什么解释器来执行，即使用哪一种 Shell

**脚本的常用执行方式** 

第一种：采用 bash 或 sh+脚本的相对路径或绝对路径（不用赋予脚本+x 权限） 

（1）sh+脚本的相对路径。sh ./helloworld.sh

 helloworld 

（2）sh+脚本的绝对路径。 sh /home/atguigu/helloworld.sh 

helloworld 

（3）bash+脚本的相对路径。 bash ./helloworld.sh 

helloworld 

（4）bash+脚本的绝对路径。 bash /home/atguigu/helloworld.sh 

helloworld 

```
注意，一定要写成 ./test.sh ，而不是 test.sh ，运行其它二进制的程序也一样，直接写 test.sh ，linux 系统会去 PATH 里寻找有没有叫 test.sh 的，而只有 /bin, /sbin, /usr/bin，/usr/sbin 等在 PATH 里，你的当前目录通常不在 PATH里，所以写成 test.sh 是会找不到命令的，要用 ./test.sh 告诉系统说，就在当前目录找。
```

第二种：采用输入脚本的绝对路径或相对路径执行脚本（必须具有可执行权限+x） 

（1）首先要赋予 helloworld.sh 脚本的+x 权限 chmod +x helloworld.sh 

（2）执行脚本

 ① 相对路径。  ./helloworld.sh

 helloworld 

② 绝对路径。 /home/atguigu/helloworld.sh

  helloworld 

注意：第一种执行方法，本质是 bash 解析器帮你执行脚本，所以脚本本身不需要执行 权限。

第二种执行方法，本质是脚本需要自己执行，所以需要执行权限。

# 变量

语法：$变量名 

$和变量名之间不能有空格。

echo $HOME  查看系统变量的值

 显示当前Shell中所有的变量 env/set

## 自定义变量

**语法**

（1）定义变量：变量名=变量值，注意，=号前后不能有空格。 

（2）撤销变量：unset 变量名。 

   (3）声明静态变量：readonly 变量，注意：不能 unset。

**定义规则**

（1)变量名称可以由字母、数字和下划线组成，但是不能以数字开头，**环境变量名建 议大写**。

（2）等号两侧不能有空格。 

（3）在 bash 中，变量默认类型都是字符串类型，无法直接进行数值运算。 

（4）变量的值如果有空格，需要使用双引号或单引号括起来。

**案例实操**

1 定义变量A 

A=5 

echo $A 

2 给A重新赋值 

A=8 

echo $A 

3 撤销变量A 

unset  A 

4 声明静态的变量B

readonly B=2

echo $B 

设定好就修改不了

5 在bash中变量默认的类型都是字符串类型，无法直接进行数值运算

C=1+2

echo $C

6 变量的值如果有空格，需要用双引号或单引号括起来

如 

D="I love banzhang"

echo $D

7 可把变量提升为全局环境变量

export B 

在helloworld.sh中加入 echo $B 

打印输出    

./helloworld.sh

## 特殊变量

**$n** 

（功能描述：n 为数字，$0 代表该脚本名称，$1-$9 代表第一到第九个参数，十以上 的参数需要用大括号包含，如${10}。）

案例实操 

 vim parameter.sh  

```shell
#!/bin/bash 
echo '==========$n==========' 
echo $0 
echo $1 
echo $2 
```

chmod 777 parameter.sh 

```
 ./parameter.sh cls xz 
 ==========$n========== 
./parameter
.sh
 cls
```

**$#** 

（功能描述：获取所有输入参数个数，常用于循环,判断参数的个数是否正确）

在上述parameter.sh后面加入 $#

**$*$@**

$* （功能描述：这个变量代表命令行中所有的参数，$*把所有的参数看成一个整体。）

 $@ （功能描述：这个变量也代表命令行中所有的参数，不过$@把每个参数区分对待。）

在上述parameter.sh后面加入 $*$@

echo  $*  $@

$*和$@的区别需要结合循环说明

```
相同点：都是引用所有参数。
不同点：只有在双引号中体现出来。假设在脚本运行时写了三个参数 1、2、3，，则 " * " 等价于 "1 2 3"（传递了一个参数），而 "@" 等价于 "1" "2" "3"（传递了三个参数）


```

**$?**

（功能描述：最后一次执行的命令的返回状态。如果这个变量的值为 0，证明上一个 命令正确执行；如果这个变量的值为非 0（具体是哪个数，由命令自己来决定），则证明 上一个命令执行不正确了。）

执行上述脚本parameter.sh 

输入echo $? 判断脚本是否正确的执行

```
atguigu@ubuntu:~$ ./helloworld.sh
hello world
atguigu@ubuntu:~$ echo $?
0
```

# 运算符

Shell 和其他编程语言一样，支持多种运算符，包括：

1. 算数运算符
2. 关系运算符
3. 布尔运算符
4. 字符串运算符
5. 文件测试运算符

 基本语法

**test  condition** 

 [ condition ]（注意 condition 前后要有空格）

 注意：条件非空即为 true，[ atguigu ]返回 true，[ ] 返回 false。

有两个 1是用test来进行测试 2 是用[ ]来进行测试  没有返回结果值 需要输入$? 来看结果对不对

## **1 算术运算符**

下表列出了常用的算术运算符，假定变量 a 为 10，变量 b 为 20：

| 运算符 | 说明                                          | 举例                          |
| :----- | :-------------------------------------------- | :---------------------------- |
| +      | 加法                                          | `expr $a + $b` 结果为 30。    |
| -      | 减法                                          | `expr $a - $b` 结果为 -10。   |
| *      | 乘法                                          | `expr $a \* $b` 结果为  200。 |
| /      | 除法                                          | `expr $b / $a` 结果为 2。     |
| %      | 取余                                          | `expr $b % $a` 结果为 0。     |
| =      | 赋值                                          | a=$b 将把变量 b 的值赋给 a。  |
| ==     | 相等。用于比较两个数字，相同则返回 true。     | [ $a == $b ] 返回 false。     |
| !=     | 不相等。用于比较两个数字，不相同则返回 true。 | [ $a != $b ] 返回 true。      |

注意：条件表达式要放在方括号之间，并且要有空格，例如: [$a==$b] 是错误的，必须写成 [ $a == $b ]。

乘号(*)前边必须加反斜杠(\)才能实现乘法运算

```
"$((运算式))" 或 "$[运算式]"
1）计算（2+3）* 4 的值
（1）$[]
atguigu@ubuntu:~$ S=$[(2+3)*4]
atguigu@ubuntu:~$ echo $S
（2）$(())
atguigu@ubuntu:~$ unset S
atguigu@ubuntu:~$ S=$(((2+3)*4))
atguigu@ubuntu:~$ echo $S
20
```

## **2 关系运算符**

关系运算符只支持数字，不支持字符串，除非字符串的值是数字。

下表列出了常用的关系运算符，假定变量 a 为 10，变量 b 为 20：

| 运算符 | 说明                                                  | 举例                       |
| :----- | :---------------------------------------------------- | :------------------------- |
| -eq    | 检测两个数是否相等，相等返回 true。                   | [ $a -eq $b ] 返回 false。 |
| -ne    | 检测两个数是否不相等，不相等返回 true。               | [ $a -ne $b ] 返回 true。  |
| -gt    | 检测左边的数是否大于右边的，如果是，则返回 true。     | [ $a -gt $b ] 返回 false。 |
| -lt    | 检测左边的数是否小于右边的，如果是，则返回 true。     | [ $a -lt $b ] 返回 true。  |
| -ge    | 检测左边的数是否大于等于右边的，如果是，则返回 true。 | [ $a -ge $b ] 返回 false。 |
| -le    | 检测左边的数是否小于等于右边的，如果是，则返回 true。 | [ $a -le $b ] 返回 true。  |

```
（1）判断23 是否大于等于 22 
1  test         
 test 23 -ge 22  
 echo $? 0 
2 [ condition ]      
   [ 23 -ge 22 ] 
   echo $?
   0 
```



## **3 布尔运算符**

下表列出了常用的布尔运算符，假定变量 a 为 10，变量 b 为 20：

| 运算符 | 说明                                                | 举例                                     |
| :----- | :-------------------------------------------------- | :--------------------------------------- |
| !      | 非运算，表达式为 true 则返回 false，否则返回 true。 | [ ! false ] 返回 true。                  |
| -o     | 或运算，有一个表达式为 true 则返回 true。           | [ $a -lt 20 -o $b -gt 100 ] 返回 true。  |
| -a     | 与运算，两个表达式都为 true 才返回 true。           | [ $a -lt 20 -a $b -gt 100 ] 返回 false。 |

**4 逻辑运算符**

以下介绍 Shell 的逻辑运算符，假定变量 a 为 10，变量 b 为 20:

| 运算符 | 说明       | 举例                                       |
| :----- | :--------- | :----------------------------------------- |
| &&     | 逻辑的 AND | [[ $a -lt 100 && $b -gt 100 ]] 返回 false  |
| \|\|   | 逻辑的 OR  | [[ $a -lt 100 \|\| $b -gt 100 ]] 返回 true |

```
多条件判断（&& 表示前一条命令执行成功时，才执行后一条命令，|| 表示上一条命令 执行失败后，才执行下一条命令） 
1 testcondition 
test atguigu && echo OK || echo notOK    OK 
test && echo OK || echo notOK       notOK
 2 [ condition ] 
[ atguigu ] && echo OK || echo notOK     OK 
[ ] && echo OK || echo notOK                    notOK
```

## **4 字符串运算符**

下表列出了常用的字符串运算符，假定变量 a 为 "abc"，变量 b 为 "efg"：

| 运算符 | 说明                                      | 举例                     |
| :----- | :---------------------------------------- | :----------------------- |
| =      | 检测两个字符串是否相等，相等返回 true。   | [ $a = $b ] 返回 false。 |
| !=     | 检测两个字符串是否相等，不相等返回 true。 | [ $a != $b ] 返回 true。 |
| -z     | 检测字符串长度是否为0，为0返回 true。     | [ -z $a ] 返回 false。   |
| -n     | 检测字符串长度是否为0，不为0返回 true。   | [ -n $a ] 返回 true。    |
| str    | 检测字符串是否为空，不为空返回 true。     | [ $a ] 返回 true。       |

## 5 文件测试运算符

 按照文件类型进行判断 

- -e 文件存在（existence）
-  -f 文件存在并且是一个常规的文件（file） 
- -d 文件存在并且是一个目录（directory）

```
3）/home/atguigu/cls.txt 目录中的文件是否存在
1  test condition
  test -e /home/atguigu/cls.txt    
 echo 
 $?
 1 
2
[ condition ] 
[ -e /home/atguigu/cls.txt ] 
echo $? 
1 
```

## 6 文件的权限

按照文件权限进行判断 -r 有读的权限（read） -w 有写的权限（write） -x 有执行的权限（execute）

```
helloworld.sh 是否具有写权限
1 test
 -w helloworld.sh   
 echo $?
 0
2 [ condition ]
 [ -w helloworld.sh ]
echo $?
0 
```

# 流程控制

## **if**

if 判断 

基本语法 

```
（1）单分支 if [ 条件判断式 ];then 
程序 fi 
或者 if [ 条件判断式 ] 
then 
 fi 
（2）多分支 
if [ 条件判断式 ]
 then
 程序 
elif [ 条件判断式 ]
 then
 程序
 else 程序
 fi 
```

注意事项： ① [ 条件判断式 ]，中括号和条件判断式之间必须有空格 ② if 后要有空格 

案例实操 输入一个数字，如果是 1，则输出 banzhang zhen shuai，如果是 2，则输出 cls zhen mei， 如果是其它，什么也不输出。 

 vim if.sh 写入以下内容 

```shell
#!/bin/bash
 if [ $1 -eq 1 ] 
then 
echo "banzhang zhen shuai" 
elif [ $1 -eq 2 ]
 then 
echo "cls zhen mei"
 fi 
```

```shell
chmod 777 if.sh    
 ./if.sh 1 
banzhang zhen shuai 
./if.sh 2 
cls zhen mei
```

## **case**

case $变量名 in

```
"值 1"）
 如果变量的值等于值 1，则执行程序 1 
;;
 "值 2"）
 如果变量的值等于值 2，则执行程序 2 
;;
 …省略其他分支
*）
如果变量的值都不是以上的值，则执行此程序 ;; 
esac
```

 注意事项：

（1）case 行尾必须为单词“in”，每一个模式匹配必须以右括号“）”结束。

 （2）双分号“;;”表示命令序列结束，相当于 C 中的 break。 

（3）最后的“）”表示默认模式，相当于 C 中的 default。

案例

输入一个数字，如果是 1，则输出 banzhang，如果是 2，则输出 cls，如果是其它，输出 renyao。  vim case.sh

```shell
#!/bin/bash
case $1 in
"1")
 echo "banzhang"
;;
"2")
 echo "cls"
;;
*)
 echo "renyao"
;;
esac
```

chmod 777 case.sh 

./case.sh 1 

./case.sh 2

./case.sh 3

## **fore **

1 for ((初始值;循环控制条件;变量变化)) 

do 程序

 done

vim for1.sh

```shell
#!/bin/bash
sum=0
for((i=0;i<=100;i++))
do
 sum=$[$sum+$i]
done
echo $sum
```

chmod 777 for1.sh

./for1.sh

2 for 变量 in 值 1 值 2 值 3…

 do

 程序

 done

vim for2.sh

```
#!/bin/bash
#打印数字
for i in cls mly wls
do
 echo "ban zhang love $i"
done
zhuxiaoyi@zhuxiaoyi-virtual-machine:~/shell$ ./for2.sh 
ban zhang love cls
ban zhang love myl
ban zhang love wls
```

3 比较$*和$@区别 *

***1 $*和$@都表示传递给函数或脚本的所有参数，不被双引号“”包含时，都以$1 $2 …$n 的形式输出所有参数**。

vim for3.sh

```shell
#!/bin/bash
echo '=============$*============='
for i in $*
do
 echo "ban zhang love $i"
done
echo '=============$@============='
for j in $@
do 
 echo "ban zhang love $j"
done

zhuxiaoyi@zhuxiaoyi-virtual-machine:~/shell$ ./for3.sh 1 2 3
=========$*========
ban zhang love 1
ban zhang love 2
ban zhang love 3
========$@=========
ban zhang love 1
ban zhang love 2
ban zhang love 3
```

**2 当它们被双引号“”包含时，$*会将所有的参数作为一个整体，以“$1 $2 …$n”的形 式输出所有参数；$@会将各个参数分开，以“$1” “$2”…“$n”的形式输出所有参数**

vim for4.sh

```shell
#!/bin/bash
echo '=============$*============='
for i in "$*"
#$*中的所有参数看成是一个整体，所以这个 for 循环只会循环一次
do
 echo "ban zhang love $i"
done
echo '=============$@============='
for j in "$@"
#$@中的每个参数都看成是独立的，所以“$@”中有几个参数，就会循环几次
do
 echo "ban zhang love $j"
done

zhuxiaoyi@zhuxiaoyi-virtual-machine:~/shell$ ./for4.sh cls mly wls
==========$*==========
ban zhang love cls mly wls
========$@===========
ban zhang love cls
ban zhang love mly
ban zhang love wls
```

## **while**

基本语法

```
while
 [ 条件判断式 ] 
do 程序
 done
```

案例实操

vim while.sh

```
#!/bin/bash
sum=0
i=1
while [ $i -le 100 ]
do
 sum=$[$sum+$i]
 i=$[$i+1]
done
echo $sum
```

## 退出循环

## 跳出循环

在循环过程中，有时候需要在未达到循环结束条件时强制跳出循环，Shell使用两个命令来实现该功能：break和continue。

**break命令**

break命令允许跳出所有循环（终止执行后面的所有循环）。

下面的例子中，脚本进入死循环直至用户输入数字大于5。要跳出这个循环，返回到shell提示符下，需要使用break命令。

```
#!/bin/bash
while :
do
    echo -n "输入 1 到 5 之间的数字:"
    read aNum
    case $aNum in
        1|2|3|4|5) echo "你输入的数字为 $aNum!"
        ;;
        *) echo "你输入的数字不是 1 到 5 之间的! 游戏结束"
            break
        ;;
    esac
done
```

执行以上代码，输出结果为：

```
输入 1 到 5 之间的数字:3
你输入的数字为 3!
输入 1 到 5 之间的数字:7
你输入的数字不是 1 到 5 之间的! 游戏结束
```

**continue**

continue命令与break命令类似，只有一点差别，它不会跳出所有循环，仅仅跳出当前循环。

对上面的例子进行修改：

```
#!/bin/bash
while :
do
    echo -n "输入 1 到 5 之间的数字: "
    read aNum
    case $aNum in
        1|2|3|4|5) echo "你输入的数字为 $aNum!"
        ;;
        *) echo "你输入的数字不是 1 到 5 之间的!"
            continue
            echo "游戏结束"
        ;;
    esac
done
```

运行代码发现，当输入大于5的数字时，该例中的循环不会结束，语句 **echo "Game is over!"** 永远不会被执行。

## read 

读取控制台输入输出

read   

-p  指定读取值时的提示符

-t 指定读取值时等待的时间 如果-t不加表示一直等待

变量 指定读取值的变量名 

7秒内读取控制台的输入

vim read.sh

```
#!/bin/bash
read -t 7 -p "Enter your name in 7 seconds :" NN
echo $NN
atguigu@ubuntu:~$ chmod 777 read.sh
atguigu@ubuntu:~$ ./read.sh
Enter your name in 7 seconds : atguigu
atguigu
```

# 函数

## 基本函数

**basename**

basename [string / pathname] [suffix] （功能描述：basename 命令会删掉所有的前 缀包括最后一个（‘/’）字符，然后将字符串显示出来。 

basename 可以理解为取路径里的文件名称。

 选项： suffix 为后缀，如果 suffix 被指定了，basename 会将 pathname 或 string 中的 suffix 去掉

```shell
basename /home/atguigu/banzhang.txt
banzhang.txt
basename /home/atguigu/banzhang.txt .txt
banzhang
```

**dirname**

dirname 文件绝对路径 （功能描述：从给定的包含绝对路径的文件名中去除文件名 （非目录的部分），然后返回剩下的路径（目录的部分）。）

 dirname 可以理解为取文件路径的绝对路径名称。

获取banzhang.txt在此文件夹下的绝对路径

```
dirname /home/atguigu/banzhang.txt
/home/atguigu
```

## 自定义函数

 **function**

```
基本语法 [ function ]  funname[()]
{ 
Action; 
[return int;] 
} 
```

经验技巧 （1）必须在调用函数地方之前，先声明函数，shell 脚本是逐行运行。不会像其它语言 一样先编译。 

（2）函数返回值，只能通过$?系统变量获得，可以显示加：return 返回，如果不加， 将以最后一条命令运行结果，作为返回值。return 后跟数值 n（0-255）。

计算两个输入参数的和 vim run.sh

```shell
#!/bin/bash
function sum()
{
 s=0
 s=$[$1+$2]
 echo "$s"
}
read -p "Please input the number1: " n1;
read -p "Please input the number2: " n2;
sum $n1 $n2;
保存退出。
atguigu@ubuntu:~$ chmod 777 fun.sh
atguigu@ubuntu:~$ ./fun.sh
Please input the number1: 2
Please input the number2: 5
7
```

## 函数参数

| **$#** | **传递到脚本的参数个数**                   |
| ------ | ------------------------------------------ |
| **$*** | **以一个单字符串显示所有向脚本传递的参数** |

**在Shell中，调用函数时可以向其传递参数。在函数体内部，通过 $n 的形式来获取参数的值，例如，$1表示第一个参数，$2表示第二个参数...**

**带参数的函数示例：**

```
#!/bin/bash
funWithParam(){
    echo "第一个参数为 $1 !"
    echo "第二个参数为 $2 !"
    echo "第十个参数为 $10 !"
    echo "第十个参数为 ${10} !"
    echo "第十一个参数为 ${11} !"
    echo "参数总数有 $# 个!"
    echo "作为一个字符串输出所有参数 $* !"
}
funWithParam 1 2 3 4 5 6 7 8 9 34 73
```

**输出结果：**

```
第一个参数为 1 !
第二个参数为 2 !
第十个参数为 10 !
第十个参数为 34 !
第十一个参数为 73 !
参数总数有 11 个!
作为一个字符串输出所有参数 1 2 3 4 5 6 7 8 9 34 73 !
```

# **Shell工具**

## cut

的  ^代表开始 $代表结束

cut 的工作就是“剪”，具体的说就是在文件中负责剪切数据用的。

cut 命令从文件的 每一行剪切字节、字符和字段并将这些字节、字符和字段输出。

cut [选项参数]

-f 提取第几列

-d 分隔符 按照指定分隔符分割列

-c 按字符进行切割后加n表示第几列

```Linux
（1）数据准备
atguigu@ubuntu:~$ vim cut.txt
dong shen
guan zhen
wo wo1
lai lai1
le le1
（2）切割 cut.txt 第一列
atguigu@ubuntu:~$ cut -d " " -f 1 cut.txt
dong
guan
wo
lai
le
（3）切割 cut.txt 第二、三列
atguigu@ubuntu:~$ cut -d " " -f 2,3 cut.txt
shen
zhen
wo1
lai1
le1
（4）在 cut.txt 文件中切割出 guan
atguigu@ubuntu:~$ cat cut.txt | grep guan | cut -d " " -f 1
guan
（5）选取系统 PATH 变量值，第 2 个“：”开始后的所有路径：
atguigu@ubuntu:~$ echo $PATH
/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr
/games:/usr/local/games:/snap/bin
atguigu@ubuntu:~$ echo $PATH | cut -d ":" -f 3-
3-代表往后所有的 3代表就这个一个
/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/b
in
（6）切割 ifconfig 后打印的 IP 地址
atguigu@ubuntu:~$ ifconfig ens33 | grep netmask | cut -d "i" -f 2
| cut -d " " -f 2
192.168.10.150
```

## **awk**

一个强大的文本分析工具，把文件逐行的读入，以空格为默认分隔符将每行切片，切开 的部分再进行分析处理。

awk [选项参数] ‘/pattern1/{action1} /pattern2/{action2}...’ filename pattern：

awk -F : '//{}' 文件名   标准格式

print输出

**-F 指定输入文件的分隔符**

**-v 赋值一个用户定义变量**

表示 awk 在数据中查找的内容，就是匹配模式。

 action：在找到匹配内容时所执行的一系列命令。

BEGIN AND    添加添加一些内容

**例题1** 

```
（1）数据准备
atguigu@ubuntu:~$ sudo cp /etc/passwd ./
passwd 数据的含义
用户名:密码(加密过后的):用户 id:组 id:注释:用户家目录:shell 解析器
（2）搜索 passwd 文件以 root 关键字开头的所有行，并输出该行的第 7 列。
atguigu@ubuntu:~$ awk -F : '/^root/{print $7}' passwd
/bin/bash
（3）搜索 passwd 文件以 root 关键字开头的所有行，并输出该行的第 1 列和第 7 列，
中间以“，”号分割。
atguigu@ubuntu:~$ awk -F : '/^root/{print $1","$7}' passwd
root,/bin/bash
注意：只有匹配了 pattern 的行才会执行 action。
（4）只显示/etc/passwd 的第一列和第七列，以逗号分割，且在所有行前面添加列名
user，shell 在最后一行添加"dahaige，/bin/zuishuai"。
atguigu@ubuntu:~$ awk -F : 'BEGIN{print "user, shell"} {print
$1","$7} END{print "dahaige,/bin/zuishuai"}' passwd
user, shell
daemon,/usr/sbin/nologin
bin,/usr/sbin/nologin
。。。
sunwukong,/bin/bash
dahaige,/bin/zuishuai
注意：BEGIN 在所有数据读取行之前执行；END 在所有数据执行之后执行。
（5）将 passwd 文件中的用户 id 增加数值 1 并输出
atguigu@ubuntu:~$ awk -v i=1 -F : '{print $3+i}' passwd
```

内置变量 变量 说明

 FILENAME 文件名

 NR 已读的记录数（行号）

 NF 浏览记录的域的个数（切割后，列的个数）

**例题2** 

```
（1）统计 passwd 文件名，每行的行号，每行的列数
atguigu@ubuntu:~$ awk -F : '{print "filename:" FILENAME 
",linenum:" NR ",col:"NF}' passwd
filename:passwd,linenum:1,col:7
filename:passwd,linenum:2,col:7
filename:passwd,linenum:3,col:7
。。。
（2）查询 ifconfig 命令输出结果中的空行所在的行号
atguigu@ubuntu:~$ ifconfig | awk '/^$/{print NR}'
9
18
（3）切割 IP
atguigu@ubuntu:~$ ifconfig ens33 | awk -F " " '/inet /{print $2}'
192.168.10.150
```

# **正则表达式**

正则表达式使用单个字符串来描述、匹配一系列符合某个语法规则的字符串。在很多文 本编辑器里，正则表达式通常被用来检索、替换那些符合某个模式的文本。在 Linux 中， grep，sed，awk 等命令都支持通过正则表达式进行模式匹配。

- ^  匹配一行的开头
- $ 匹配一行的结束
- ^$ 匹配空行
- .匹配任意一个字符
- *不单独使用，他和上一个字符连用，表示匹配上一个字符 0 次或多次
- .*匹配任意字符
-  [ ]表示匹配某个范围内的字符[a-z]------匹配一个 a-z 之间的字符 [a-z]* ------匹配任意长度的字母字符串 [a-c, e-f]-匹配 a-c 或者 e-f 之间的任意字符，[6,8]------匹配 6 或者 8 [0-9]------匹配一个 0-9 的数字
- 特殊字符 \\  表示转义，并不会单独使用。由于所有特殊字符都有其特定匹配模式，当我们想匹配 某一特殊字符本身时（例如，我想找出所有包含 '$' 的行）

**10.4 经典正则表达式** 

**#邮箱正则** 

**^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(.[a-zA-Z0-9_-]+)+$ #手机号正则**

 **/^1((34[0-8])|(8\d{2})(([35][0-35-9]|4[579]|66|7[35678]|9[1389])\d{1}))\d{7}$**

# **案例**

## **题目 1**

**要求：**
**分析 Apache 日志文件 `/var/log/apache2/access.log`，统计：**

1. **前 10 大 IP 的请求量**
2. **各状态码的出现次数**
3. **耗时最长的 5 个请求**

**答案：**

**bash**

```bash
#!/bin/bash

# 初始化关联数组
declare -A ip_counts status_counts
max_time=0
top_requests=()

while read -r line; do
    # 提取IP
    ip=$(echo "$line" | awk '{print $1}')
    ip_counts["$ip"]=$((ip_counts["$ip"]+1))
    
    # 提取状态码
    status=$(echo "$line" | awk '{print $9}')
    status_counts["$status"]=$((status_counts["$status"]+1))
    
    # 提取耗时
    time=$(echo "$line" | awk '{print $NF}' | sed 's/[ms]//g')
    if ((time > max_time)); then
        max_time=$time
        top_requests+=("$line")
    fi
done < /var/log/apache2/access.log

# 输出结果
echo "Top 10 IPs:"
printf "%-15s %s\n" "IP" "Count"
echo "${!ip_counts[@]}" | tr ' ' '\n' | sort -nr -k2 -t: | head -n10

echo -e "\nStatus Codes:"
printf "%-10s %s\n" "Code" "Count"
for code in "${!status_counts[@]}"; do
    printf "%-10s ${status_counts[$code]}\n" "$code"
done

echo -e "\nTop 5 Slowest Requests:"
echo "${top_requests[@]}" | tr ' ' '\n' | sort -nr -k$(echo "$line" | awk '{print NF}') | head -n5
```

## **题目 2**

**要求：**
**编写脚本并行处理 `/data` 目录下的所有 `.txt` 文件，将每行的单词数统计结果保存到 `result.log`，并限制同时运行的进程数不超过 5。**

**答案：**

**bash**

```bash
#!/bin/bash

# 信号处理函数
trap 'wait; exit' INT TERM

# 初始化进程池
for ((i=0; i<5; i++)); do
    ((i==0)) && echo "Processing started..."
    {
        while IFS= read -r file; do
            word_count=$(wc -w < "$file")
            echo "$file: $word_count"
        done
    } &
done

# 提交任务
find /data -type f -name "*.txt" | xargs -I {} echo {} | sed 's/ /\\ /g' | tee /dev/fd/3 | {
    while read -r file; do
        read -u3
        echo "$file"
    done
}

wait
```

## **题目 3**

**要求：**
**编写递归函数生成第 n 项斐波那契数，使用缓存机制优化重复计算，并支持批量生成多个项。**

**答案：**

**bash**

```bash
#!/bin/bash

declare -A fib_cache

fib() {
    local n=$1
    if [[ -z "$fib_cache[$n]" ]]; then
        if ((n <= 1)); then
            fib_cache[$n]=$n
        else
            fib $((n-1))
            fib $((n-2))
            fib_cache[$n]=$((fib_cache[n-1] + fib_cache[n-2]))
        fi
    fi
    echo "${fib_cache[$n]}"
}

# 批量生成
for i in {1..10}; do
    echo "Fib($i) = $(fib $i)"
done
```

## **题目 4**

**要求：**
**每 5 分钟检查服务器网络状态，若发现以下情况则发送邮件报警：**

1. **连续 3 次 ping 不通网关**
2. **某个端口（如 80）无法访问**

**答案：**

**bash**

```bash
#!/bin/bash

GATEWAY="192.168.1.1"
PORT=80
RETRY=3
EMAIL="admin@example.com"

check_gateway() {
    for ((i=0; i<$RETRY; i++)); do
        ping -c1 $GATEWAY &>/dev/null || return 1
    done
    return 0
}

check_port() {
    timeout 2 nc -z localhost $PORT &>/dev/null || return 1
}

while true; do
    if ! check_gateway || ! check_port; then
        echo "Network alert: $(date)" | mail -s "Server Network Down" $EMAIL
    fi
    sleep 300
done
```

## **题目 5**

**要求：**
**编写脚本自动部署 Web 应用到多台服务器，步骤包括：**

1. **检查服务器状态**
2. **上传代码并解压**
3. **重启服务**
4. **验证部署结果**

**答案：**

**bash**

```bash
#!/bin/bash

servers=("server1" "server2" "server3")

for server in "${servers[@]}"; do
    # 检查SSH连通性
    if ! ssh -q $server exit; then
        echo "$server is unreachable"
        continue
    fi

    # 上传代码
    scp -r /local/app $server:/remote/
    ssh $server "tar -xzf /remote/app.tar.gz -C /remote/"

    # 重启服务
    ssh $server "sudo systemctl restart apache2"

    # 验证
    response=$(curl -s -o /dev/null -w "%{http_code}" http://$server)
    if [[ "$response" -eq 200 ]]; then
        echo "$server deployment successful"
    else
        echo "$server deployment failed"
    fi
done
```

**题目 6**

**要求：**
**处理 CSV 文件 `data.csv`，将其转换为 JSON 格式，要求：**

1. **过滤掉空行和注释行（以 #开头）**
2. **将数值字段转换为浮点数**
3. **处理日期格式（如 `2023-10-01` 转为 `10/01/2023`）**

**答案：**

**bash**

```bash
#!/bin/bash

awk -F, '
BEGIN {
    print "["
}
!/^#/ && NF>0 {
    gsub(/-/, "/", $3)
    printf "  {\n    \"id\": %d,\n    \"name\": \"%s\",\n    \"date\": \"%s\",\n    \"value\": %.2f\n  },\n", $1, $2, $3, $4
}
END {
    print "]"
}' data.csv | sed '$s/,$//'
```

## **题目 7**

**要求：**
**编写脚本实时监控内存使用情况，当某个进程连续 3 次内存使用率超过阈值（如 80%）时，自动终止该进程并记录日志。**

**答案：**

**bash**

```bash
#!/bin/bash

THRESHOLD=80
INTERVAL=60
LOG_FILE="/var/log/memory_leak.log"

while true; do
    # 获取内存使用TOP10进程
    top_process=$(ps -eo pid,user,%mem,command --sort=-%mem | head -n2)
    
    # 解析进程信息
    pid=$(echo "$top_process" | awk 'NR==2 {print $1}')
    mem_usage=$(echo "$top_process" | awk 'NR==2 {print $3}')
    
    # 检查阈值
    if (( $(echo "$mem_usage > $THRESHOLD" | bc) )); then
        ((count++))
        if ((count >=3)); then
            echo "$(date) - Terminating process $pid ($mem_usage%)" | tee -a "$LOG_FILE"
            kill -9 $pid
            count=0
        fi
    else
        count=0
    fi
    
    sleep "$INTERVAL"
done
```

## **题目 8**

**要求：**
**编写脚本并行下载多个 URL，支持以下功能：**

1. **限制最大并发数（如 5）**
2. **显示每个文件的下载进度**
3. **失败重试机制**

**答案：**

**bash**

```bash
#!/bin/bash

urls=(
    "http://example.com/file1.zip"
    "http://example.com/file2.tar.gz"
)
max_concurrent=5

# 初始化信号处理
trap 'wait; exit' INT TERM

# 下载函数
download() {
    local url=$1
    local filename=$(basename "$url")
    local tempfile="$filename.part"
    
    wget --no-check-certificate -O "$tempfile" "$url" --progress=bar:force
    if [[ $? -eq 0 ]]; then
        mv "$tempfile" "$filename"
    else
        echo "Failed to download $url"
    fi
}

# 提交任务
for url in "${urls[@]}"; do
    while true; do
        # 检查当前并发数
        running=$(jobs -r | wc -l)
        if ((running < max_concurrent)); then
            download "$url" &
            break
        fi
        sleep 1
    done
done

wait
```

## **题目 9**

**要求：**
**设计一个测试框架，包含以下功能：**

1. **支持测试用例的模块化管理**
2. **自动生成测试报告**
3. **支持测试依赖管理**

**答案：**

**bash**

```bash
#!/bin/bash

# 测试用例库
source test_functions.sh

# 初始化报告
echo "Test Report - $(date)" > report.html

# 执行测试用例
test_case1() {
    test_function1
    [ $? -eq 0 ] && echo "PASS" || echo "FAIL"
}

test_case2() {
    test_function2
    [ $? -eq 0 ] && echo "PASS" || echo "FAIL"
}

# 主流程
run_test() {
    local test_name=$1
    echo "<h2>$test_name</h2>" >> report.html
    local result=$("$test_name")
    echo "<p>Result: $result</p>" >> report.html
}

run_test test_case1
run_test test_case2
```

## **题目 10**

**要求：**
**编写脚本优化数据库服务器性能，包括：**

1. **调整内核参数（如 `vm.swappiness`）**
2. **设置 SSD 的 IO 调度策略为 `none`**
3. **预加载常用库文件**

**答案：**

**bash**

```bash
#!/bin/bash

# 调整内核参数
sysctl -w vm.swappiness=10
echo "vm.swappiness = 10" >> /etc/sysctl.conf

# 设置IO调度策略
for disk in /sys/block/sd?/queue/scheduler; do
    echo "none" | sudo tee "$disk"
done

# 预加载库文件
preload=$(ldconfig -p | awk '{print $1}' | grep -v '^libc')
echo "$preload" > /etc/ld.so.preload
```

**关键技术点总结**

1. **关联数组：用于高效统计日志中的 IP 和状态码。**
2. **子 shell 与信号处理：实现并发控制和优雅退出。**
3. **缓存优化：通过数组缓存减少递归计算量。**
4. **网络命令组合：`curl`、`nc`、`ssh` 等实现远程监控。**
5. **正则与文本处理：`awk`、`sed` 处理复杂格式转换。**
6. **多线程下载：利用 `jobs` 和 `wait` 实现并发控制。**
7. **内核参数调整：通过 `sysctl` 优化系统性能。**

## **题目11**

**复杂的文件内容对比与更新**

**有两个目录 `dir1` 和 `dir2`，它们包含大量的文本文件，文件名相同。需要对比 `dir1` 和 `dir2` 中同名文件的内容，若 `dir2` 中的文件内容与 `dir1` 不同，且 `dir2` 文件中的每一行都以 `[NEW]` 开头，则将 `dir2` 中的文件内容覆盖到 `dir1` 中对应的文件；若 `dir2` 文件部分行以 `[NEW]` 开头，部分行不以 `[NEW]` 开头，则只将以 `[NEW]` 开头的行更新到 `dir1` 中对应文件的相同行位置。**

**答案**

**bash**

```bash
for file in $(find dir1 -type f -printf "%f\n"); do
    if [ -f "dir2/$file" ]; then
        diff_result=$(diff "dir1/$file" "dir2/$file")
        if [ -n "$diff_result" ]; then
            all_new=true
            while IFS= read -r line; do
                if ! [[ $line =~ ^\[NEW\] ]]; then
                    all_new=false
                    break
                fi
            done < "dir2/$file"
            if $all_new; then
                cp "dir2/$file" "dir1/$file"
            else
                temp_file=$(mktemp)
                i=1
                while IFS= read -r line1; do
                    line2=$(sed -n "${i}p" "dir2/$file")
                    if [[ $line2 =~ ^\[NEW\] ]]; then
                        echo "${line2#[NEW]}" >> "$temp_file"
                    else
                        echo "$line1" >> "$temp_file"
                    fi
                    i=$((i + 1))
                done < "dir1/$file"
                mv "$temp_file" "dir1/$file"
            fi
        fi
    fi
done
```

**解释**

1. **遍历 `dir1` 中的文件：使用 `find` 命令找出 `dir1` 下的所有文件，并依次处理。**
2. **检查 `dir2` 中是否存在同名文件：如果存在，则使用 `diff` 命令对比两个文件内容。**
3. **判断 `dir2` 文件内容是否全以 `[NEW]` 开头：通过逐行检查实现。若全是，则直接覆盖 `dir1` 中的文件。**
4. **部分更新：若部分行以 `[NEW]` 开头，使用临时文件逐行处理，将以 `[NEW]` 开头的行更新到 `dir1` 文件对应位置。**

## **题目12**

**多条件筛选与数据聚合**

**有一个大型日志文件 `large_log.log`，每行格式为 `时间戳 进程 ID 操作类型 操作结果 数据量`，例如 `2024-10-01 12:30:00 1234 read success 1024`。需要筛选出在特定时间段（如 2024 - 10 - 01 12:00:00 到 2024 - 10 - 01 13:00:00）内，操作类型为 `write` 且操作结果为 `success` 的记录，然后按进程 ID 分组，统计每个进程的总数据量，并按总数据量从大到小排序。**

**bash**

```bash
awk -v start="2024-10-01 12:00:00" -v end="2024-10-01 13:00:00" '$1 " " $2 >= start && $1 " " $2 <= end && $3 == "write" && $4 == "success" { total[$5]+=$6 } END { for (pid in total) print pid, total[pid] }' large_log.log | sort -k2 -nr
```

**解释**

1. **筛选特定时间段、操作类型和操作结果的记录：使用 `awk` 的条件判断语句，结合时间范围、操作类型和操作结果进行筛选。**
2. **按进程 ID 分组统计总数据量：使用 `awk` 的数组 `total` 以进程 ID 为索引，累加每个进程的数据量。**
3. **排序输出：使用 `sort` 命令按总数据量从大到小排序。**

## **题目13**

**递归文件权限修复与备份**

**题目描述**

**在目录 `target_dir` 下递归查找所有文件和目录，对于所有文件，若其权限不是 `644`，则将其权限修改为 `644`，并将修改前的权限信息备份到一个日志文件 `permission_backup.log` 中；对于所有目录，若其权限不是 `755`，则将其权限修改为 `755`，同样将修改前的权限信息备份到日志文件中。**



```bash
find target_dir -print0 | while IFS= read -r -d '' item; do
    current_perm=$(stat -c "%a" "$item")
    if [ -f "$item" ]; then
        if [ "$current_perm" != "644" ]; then
            echo "$item had permission $current_perm before change" >> permission_backup.log
            chmod 644 "$item"
        fi
    elif [ -d "$item" ]; then
        if [ "$current_perm" != "755" ]; then
            echo "$item had permission $current_perm before change" >> permission_backup.log
            chmod 755 "$item"
        fi
    fi
done
```

**解释**

1. **递归遍历目录：使用 `find` 命令结合 `while` 循环，以空字符作为分隔符，确保能正确处理包含特殊字符的文件名。**
2. **获取当前权限：使用 `stat -c "%a"` 命令获取文件或目录的当前权限。**
3. **判断文件或目录类型并修改权限：根据文件或目录类型，对比当前权限与目标权限，若不同则修改权限并记录备份信息。**

## **题目 14**

**跨主机文件同步与冲突处理**

**需要将本地目录 `local_dir` 同步到远程主机 `remote_host` 的目录 `remote_dir` 中。在同步过程中，如果出现文件冲突（即文件名相同但内容不同），需要将本地文件和远程文件进行合并，合并规则为：若本地文件中的行以 `[LOCAL]` 开头，远程文件中的行以 `[REMOTE]` 开头，则将这些行分别保留在合并后的文件中，其他行根据行号交替合并。最后将合并后的文件同步到远程主机。**

**答案**

**bash**

```bash
rsync -avn --itemize-changes local_dir/ remote_host:remote_dir/ | grep "^c" | awk '{print $2}' | while read -r file; do
    local_file="local_dir/$file"
    remote_file="remote_host:remote_dir/$file"
    temp_local=$(mktemp)
    temp_remote=$(mktemp)
    scp "$local_file" "$temp_local"
    scp "$remote_file" "$temp_remote"
    merged_file=$(mktemp)
    local_lines=$(wc -l < "$temp_local")
    remote_lines=$(wc -l < "$temp_remote")
    max_lines=$(( local_lines > remote_lines ? local_lines : remote_lines ))
    for ((i = 1; i <= max_lines; i++)); do
        local_line=$(sed -n "${i}p" "$temp_local")
        remote_line=$(sed -n "${i}p" "$temp_remote")
        if [[ $local_line =~ ^\[LOCAL\] ]]; then
            echo "$local_line" >> "$merged_file"
        elif [[ $remote_line =~ ^\[REMOTE\] ]]; then
            echo "$remote_line" >> "$merged_file"
        else
            [ -n "$local_line" ] && echo "$local_line" >> "$merged_file"
            [ -n "$remote_line" ] && echo "$remote_line" >> "$merged_file"
        fi
    done
    scp "$merged_file" "$remote_file"
    rm "$temp_local" "$temp_remote" "$merged_file"
done
rsync -av local_dir/ remote_host:remote_dir/
```

**解释**

1. **找出冲突文件：使用 `rsync -avn --itemize-changes` 模拟同步过程，找出冲突文件（以 `c` 开头的行）。**
2. **下载本地和远程文件：使用 `scp` 命令将本地和远程的冲突文件下载到临时文件中。**
3. **合并文件：按行号交替合并文件，保留以 `[LOCAL]` 和 `[REMOTE]` 开头的行。**
4. **上传合并后的文件：使用 `scp` 命令将合并后的文件上传到远程主机。**
5. **完成同步：使用 `rsync -av` 命令完成最终的同步。**

# **总结**