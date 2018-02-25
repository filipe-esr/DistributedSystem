echo "	> Sending the proper jar to the correct workstation"
cd ../jars
sshpass -p sd_sockets scp -o StrictHostKeyChecking=no generalRepository.jar sd0304@l040101-ws01.ua.pt:~
sshpass -p sd_sockets scp -o StrictHostKeyChecking=no -r lib sd0304@l040101-ws01.ua.pt:~
sshpass -p sd_sockets scp -o StrictHostKeyChecking=no museum.jar sd0304@l040101-ws03.ua.pt:~
sshpass -p sd_sockets scp -o StrictHostKeyChecking=no -r lib sd0304@l040101-ws03.ua.pt:~
sshpass -p sd_sockets scp -o StrictHostKeyChecking=no concentrationSite.jar sd0304@l040101-ws04.ua.pt:~
sshpass -p sd_sockets scp -o StrictHostKeyChecking=no -r lib sd0304@l040101-ws04.ua.pt:~
sshpass -p sd_sockets scp -o StrictHostKeyChecking=no controlSite.jar sd0304@l040101-ws05.ua.pt:~
sshpass -p sd_sockets scp -o StrictHostKeyChecking=no -r lib sd0304@l040101-ws05.ua.pt:~
sshpass -p sd_sockets scp -o StrictHostKeyChecking=no assaultParty.jar sd0304@l040101-ws07.ua.pt:~
sshpass -p sd_sockets scp -o StrictHostKeyChecking=no -r lib sd0304@l040101-ws07.ua.pt:~
sshpass -p sd_sockets scp -o StrictHostKeyChecking=no AssaultParty1.jar sd0304@l040101-ws07.ua.pt:~
sshpass -p sd_sockets scp -o StrictHostKeyChecking=no MT.jar sd0304@l040101-ws09.ua.pt:~
sshpass -p sd_sockets scp -o StrictHostKeyChecking=no -r lib sd0304@l040101-ws09.ua.pt:~
sshpass -p sd_sockets scp -o StrictHostKeyChecking=no T.jar sd0304@l040101-ws10.ua.pt:~
sshpass -p sd_sockets scp -o StrictHostKeyChecking=no -r lib sd0304@l040101-ws10.ua.pt:~

cd ../../

#echo "  > Cleaning logs from the server where Logging is going to run"
#sshpass -p sd_sockets ssh sd0304@l040101-ws.ua.pt -o StrictHostKeyChecking 'rm *.txt'

echo "	> Executing each jar file on the proper workstation"
sshpass -p sd_sockets ssh sd0304@l040101-ws01.ua.pt -o StrictHostKeyChecking=no 'java -jar generalRepository.jar' &
PID_Logging=$!
sshpass -p sd_sockets ssh sd0304@l040101-ws03.ua.pt -o StrictHostKeyChecking=no 'java -jar museum.jar' &
sshpass -p sd_sockets ssh sd0304@l040101-ws04.ua.pt -o StrictHostKeyChecking=no 'java -jar concentrationSite.jar' &
sshpass -p sd_sockets ssh sd0304@l040101-ws05.ua.pt -o StrictHostKeyChecking=no 'java -jar controlSite.jar' &
sshpass -p sd_sockets ssh sd0304@l040101-ws07.ua.pt -o StrictHostKeyChecking=no 'java -jar assaultParty.jar' &
sshpass -p sd_sockets ssh sd0304@l040101-ws07.ua.pt -o StrictHostKeyChecking=no 'java -jar AssaultParty1.jar' &
sshpass -p sd_sockets ssh sd0304@l040101-ws09.ua.pt -o StrictHostKeyChecking=no 'java -jar MT.jar' &
sshpass -p sd_sockets ssh sd0304@l040101-ws10.ua.pt -o StrictHostKeyChecking=no 'java -jar T.jar' &
echo "	> Waiting for simulation to end (generate a logging file).."
wait $PID_Logging
