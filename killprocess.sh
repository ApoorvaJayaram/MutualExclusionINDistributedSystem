remoteuser=axj143730
remotecomputer1=dc11.utdallas.edu
remotecomputer2=dc12.utdallas.edu
remotecomputer3=dc13.utdallas.edu
remotecomputer4=dc14.utdallas.edu
remotecomputer5=dc15.utdallas.edu
remotecomputer6=dc16.utdallas.edu
remotecomputer7=dc17.utdallas.edu
remotecomputer8=dc18.utdallas.edu
remotecomputer9=dc19.utdallas.edu
remotecomputer10=dc20.utdallas.edu

#port1=3332
#port2=5678
#port3=5231
#port4=2311
#port5=3124
#port6=3341
#port7=3342

ssh -l  "$remoteuser" "$remotecomputer1" "killall java" &
ssh -l  "$remoteuser" "$remotecomputer2" "killall java" &
ssh -l  "$remoteuser" "$remotecomputer3" "killall java" &
ssh -l  "$remoteuser" "$remotecomputer4" "killall java" &
ssh -l  "$remoteuser" "$remotecomputer5" "killall java" &
ssh -l  "$remoteuser" "$remotecomputer5" "killall java" &
ssh -l  "$remoteuser" "$remotecomputer6" "killall java" &
ssh -l  "$remoteuser" "$remotecomputer7" "killall java" &
ssh -l  "$remoteuser" "$remotecomputer8" "killall java" &
ssh -l  "$remoteuser" "$remotecomputer9" "killall java" &
ssh -l  "$remoteuser" "$remotecomputer10" "killall java" &
