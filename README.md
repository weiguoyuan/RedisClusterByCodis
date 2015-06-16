# RedisClusterByCodis
Redis cluster create by codis
1.codis集群搭建
  1.1 安装go1.3.1 
      CentOS 7.0 安装go 1.3.1
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

      

  1.4 安装zookeeper 集群 

       3个机器上每个机器上都安装一个zookeeper

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

5. 配置节点信息

   https://github.com/wandoulabs/codis/blob/master/doc/tutorial_zh.md

   也可以在 /data/gopkg/src/github.com/wandoulabs/codis/sample 下配置start_redis.sh add_group.sh 等文件

 

参考：http://navyaijm.blog.51cto.com/4647068/1637688

        https://github.com/wandoulabs/codis/blob/master/doc/tutorial_zh.md
