1. Codis 集群搭建

   1.1 安装go1.3.1 CentOS 7.0 安装go 1.3.1

       1.下载go安装包 golang中国上下载 下载到Downloads下

       2. 解压 tar -zxf go1.3.1.linux-amd64.tar.gz -C /usr/local/

       3. 修改 etc/profile 文件在文件后加入 export的几行，在unset下面直接加，不要有空行

          unset i
          unset -f pathmunge
          export GOROOT=/usr/local/go
          export PATH=$GOROOT/bin:$PATH
          export GOPATH=/data/gopkg

       4. 然后执行 source /etc/profile 刷新配置文件

       5. 运行命令 go 测试go是否安装成功

       6. 在usr/local/go/test 下 运行 go run helloworld.go 测试

   1.2 安装git yum -y install git
  
   1.3 配置hosts文件 3个机器都是相同的配置 

       cd /etc
       vi hosts
        
       [will@weiguoyuan etc]$ more hosts
       127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4
       ::1         localhost localhost.localdomain localhost6 localhost6.localdomain6
       10.64.4.57  weiguoyuan
       10.64.4.95  weiguoyuan2
       10.64.4.99  hemy

       还需要配置windows下的hosts文件 否则在windows下的jodis客户端访问codis集群机器找不到主机名对应的ip

       C:\Windows\System32\drivers\etc 
       
       # Copyright (c) 1993-2009 Microsoft Corp.
       #
       # This is a sample HOSTS file used by Microsoft TCP/IP for Windows.
       #
       # This file contains the mappings of IP addresses to host names. Each
       # entry should be kept on an individual line. The IP address should
       # be placed in the first column followed by the corresponding host name.
       # The IP address and the host name should be separated by at least one
       # space.
       #
       # Additionally, comments (such as these) may be inserted on individual
       # lines or following the machine name denoted by a '#' symbol.
       #
       # For example:
       #
       #      102.54.94.97     rhino.acme.com          # source server
       #       38.25.63.10     x.acme.com              # x client host
       
       # localhost name resolution is handled within DNS itself.
       #	127.0.0.1       localhost
       #	::1             localhost
       
       10.64.4.57	weiguoyuan
       10.64.4.95	weiguoyuan2
       10.64.4.99	hemy


   1.4 安装zookeeper 集群  3个机器上每个机器上都安装一个zookeeper

       1. 官网下载 下载到Downloads下

       2. tar -xzf zookeeper.**.tar.gz -C /usr/local

       3. cd /usr/local/zookeeper*/conf

       4. cp zookeeper.cfg zoo.cfg

       5. vi zoo.cfg（三个zookeeper的配置文件相同）　在尾部加上节点信息 （节点之前通信）

         [will@weiguoyuan conf]$ more zoo.cfg 
         # The number of milliseconds of each tick
         tickTime=2000
         # The number of ticks that the initial 
         # synchronization phase can take
         initLimit=10
         # The number of ticks that can pass between 
         # sending a request and getting an acknowledgement
         syncLimit=5
         # the directory where the snapshot is stored.
         # do not use /tmp for storage, /tmp here is just 
         # example sakes.
         dataDir=/tmp/zookeeper
         # the port at which the clients will connect
          clientPort=2181
         # the maximum number of client connections.
         # increase this if you need to handle more clients
         #maxClientCnxns=60
         #
         # Be sure to read the maintenance section of the 
         # administrator guide before turning on autopurge.
         #
         # http://zookeeper.apache.org/doc/current/zookeeperAdmin.html#sc_maintenance
         #
         # The number of snapshots to retain in dataDir
         #autopurge.snapRetainCount=3
         # Purge task interval in hours
         # Set to "0" to disable auto purge feature
         #autopurge.purgeInterval=1
         server.1=10.64.4.57:2888:3888
         server.2=10.64.4.95:2888:3888
         server.3=10.64.4.99:2888:3888

       6. 配置zookeeper节点id 
          先启动3个机器的zookeeper zookeeper会自动生成/tmp/zookeeper文件夹
          再设置节点的myid myid对应的zoo.cfg的server.ID比如192.168.253.128机器上的myid文件内容为1（3个机器分别生成123

          echo "1" >/tmp/zookeeper/myid #3个机器上分别执行
          echo "2" >/tmp/zookeeper/myid
          echo "3" >/tmp/zookeeper/myid

       7. 启动zookeeper

           cd /usr/local/zookeeper/bin
           ./zkServer.sh start
           ./zkServer.sh stop
           ./zkServer.sh status 查看节点状态有 leader,fellower
 
   1.5 安装codis 编译 3个机器上都需安装
  
       go get -d github.com/wandoulabs/codis

       cd $GOPATH/src/github.com/wandoulabs/codis

       ./bootstrap.sh （这步比较慢 失败了可以重试）

       make gotest



2. Codis 配置

    2.1 官方文档 命令方式配置 https://github.com/wandoulabs/codis/blob/master/doc/tutorial_zh.md

    2.2 编写脚本 脚本方式配置 /data/gopkg/src/github.com/wandoulabs/codis/sample start_redis.sh add_group.sh 
   
    2.2.1 配置config.ini 3个机器都得配置

       cd /data/gopkg/src/github.com/wandoulabs/codis/sample
       vi config.ini

       [will@weiguoyuan sample]$ more config.ini 
       zk=10.64.4.57:2181,10.64.4.95:2181,10.64.4.99:2181 #zookeeper列表
       product=test
       proxy_id=proxy_1 #3个机器上分别是 proxy_1 proxy_2 proxy_3
       net_timeout=5
       dashboard_addr=weiguoyuan:18087
       coordinator=zookeeper

    2.2.2 配置 start_redis.sh 3个机器都要配置

       cd /data/gopkg/src/github.com/wandoulabs/codis/sample
       vi start_redis.sh

       [will@weiguoyuan sample]$ more start_redis.sh 
       #!/bin/sh
       nohup ../bin/codis-server ./redis_conf/6380.conf &> ./log/redis_6380.log &
       nohup ../bin/codis-server ./redis_conf/6381.conf &> ./log/redis_6381.log &
       nohup ../bin/codis-server ./redis_conf/6382.conf &> ./log/redis_6382.log &
       nohup ../bin/codis-server ./redis_conf/6383.conf &> ./log/redis_6383.log &

       echo "sleep 3s"
       sleep 3
       tail -n 30 ./log/redis_6380.log
       tail -n 30 ./log/redis_6381.log
       tail -n 30 ./log/redis_6382.log
       tail -n 30 ./log/redis_6383.log

    2.2.3 配置 add_group.sh 只需一个机器配置
    
       cd /data/gopkg/src/github.com/wandoulabs/codis/sample
       vi start_redis.sh

       [will@weiguoyuan sample]$ more add_group.sh 
       #!/bin/sh

       echo "add group 1 with a master(localhost:6381), Notice: do not use localhost when in produciton"
       ../bin/codis-config -c config.ini -L ./log/cconfig.log server add 1 10.64.4.57:6380 master
       ../bin/codis-config -c config.ini -L ./log/cconfig.log server add 1 10.64.4.95:6380 slave
       ../bin/codis-config -c config.ini -L ./log/cconfig.log server add 1 10.64.4.99:6380 slave
       ../bin/codis-config -c config.ini -L ./log/cconfig.log server add 2 10.64.4.57:6381 master
       ../bin/codis-config -c config.ini -L ./log/cconfig.log server add 2 10.64.4.95:6381 slave
       ../bin/codis-config -c config.ini -L ./log/cconfig.log server add 2 10.64.4.99:6381 slave

       echo "add group 2 with a master(localhost:6382), Notice: do not use localhost when in produciton"
       ../bin/codis-config -c config.ini -L ./log/cconfig.log server add 3 10.64.4.95:6382 master
       ../bin/codis-config -c config.ini -L ./log/cconfig.log server add 3 10.64.4.57:6382 slave
       ../bin/codis-config -c config.ini -L ./log/cconfig.log server add 3 10.64.4.99:6382 slave
       ../bin/codis-config -c config.ini -L ./log/cconfig.log server add 4 10.64.4.99:6383 master
       ../bin/codis-config -c config.ini -L ./log/cconfig.log server add 4 10.64.4.95:6383 slave
       ../bin/codis-config -c config.ini -L ./log/cconfig.log server add 4 10.64.4.57:6383 slave

    2.2.4 配置 initslot.sh 只需一个机器配置
 
       cd /data/gopkg/src/github.com/wandoulabs/codis/sample
       vi initslot.sh

       [will@weiguoyuan sample]$ more initslot.sh 
       #!/bin/sh
       echo "slots initializing..."
       ../bin/codis-config -c config.ini slot init -f
       echo "done"

       echo "set slot ranges to server groups..."
       ../bin/codis-config -c  config.ini slot range-set 0 255 1 online
       ../bin/codis-config -c  config.ini slot range-set 256 511 2 online
       ../bin/codis-config -c  config.ini slot range-set 512 767 3 online
       ../bin/codis-config -c  config.ini slot range-set 768 1023 4 online
       echo "done"
       
    2.2.5 修改 start_proxy.sh 机器1 不用修改另外两个机器修改
    
       cd /data/gopkg/src/github.com/wandoulabs/codis/sample
       vi start_proxy.sh

       [will@weiguoyuan sample]$ more start_proxy.sh 
       #!/bin/sh
       echo "shut down proxy_1..."
       ../bin/codis-config -c config.ini proxy offline proxy_1 //修改这里
       echo "done"

       echo "start new proxy..."
       nohup ../bin/codis-proxy --log-level info -c config.ini -L ./log/proxy.log  --cpu=8 --addr=0.0.0.0:190
       00 --http-addr=0.0.0.0:11000 &
       echo "done"

       echo "sleep 3s"
       sleep 3
       tail -n 30 ./log/proxy.log

    2.2.6 修改 set_proxy_online.sh 机器1 不用修改另外两个机器修改
    
       cd /data/gopkg/src/github.com/wandoulabs/codis/sample
       vi set_proxy_online.sh

       [will@weiguoyuan sample]$ more set_proxy_online.sh 
       #!/bin/sh
       echo "set proxy_1 online"
       ../bin/codis-config -c config.ini proxy online proxy_1 #修改这里
       echo "done"

    2.2.7 Codis 集群启动
        
       1.启动3个机器

       2.关闭3个机器防火墙
          CentOS防火墙分为2中 firewalld 和 iptables
          如果是firewalld systemctl stop firewalld.service
          如果是iptables  systemctl stop iptables.service

       3.启动3个机器的zookeeper 
          cd /usr/local/zookeeper/bin
          ./zkServer.sh start

       4.在没有配置add_group.sh的两个机器上(机器2和3) 
          cd /data/gopkg/src/github.com/wandoulabs/codis/sample
          ./start_redis.sh

       5.在配置add_group.sh的机器上(机器1) 上
          cd /data/gopkg/src/github.com/wandoulabs/codis/sample
          ./startall.sh

       6.在机器1上打开火狐浏览器 打开网址 http://localhost:18087/admin 可以看到节点 代理信息

       7.在机器2和3上分别启动代理
          cd /data/gopkg/src/github.com/wandoulabs/codis/sample
          ./start_proxy.sh

       8.在机器1上浏览器http://localhost:18087/admin的代理信息中 设置proxy_2 proxy_3 online

       9.可以通过windows上的jodis客户端访问Codis集群了


3. 利用Asis2生成 Webservice服务
    http://www.cnblogs.com/weixiaole/p/4372319.html

4. codis-ha

    官方文档 https://github.com/ngaut/codis-ha

    go get github.com/ngaut/codis-ha

    cd codis-ha

     go build

     codis-ha --codis-config=localhost:18087 --productName=test

   
5. 参考
        http://navyaijm.blog.51cto.com/4647068/1637688
        https://github.com/wandoulabs/codis/blob/master/doc/tutorial_zh.md
