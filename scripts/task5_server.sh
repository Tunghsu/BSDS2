
source set_path.sh
cd -
cd ../
rm -rf out/*.class
javac -d out ./*.java
java -cp out BSDSAssignment1_dongxu_han.CAServer &
ServPID="$!"
#echo "ServPID=$PID"

echo "Server should have started ..."
sleep 1

trap "kill $ServPID $DiagPID" SIGINT SIGTERM 

wait
