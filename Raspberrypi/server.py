import socket
import time
import RPi.GPIO as GPIO
from time import sleep

GPIO.setmode(GPIO.BCM) #GPIO 모드 설정
GPIO.setup(21, GPIO.OUT) # GPIO 21핀을 output으로 설정
GPIO.setup(18, GPIO.IN) # GPIO 18핀을 input으로 설정


host = '입력' # 호스트 ip
port = 8091 #port

server_sock = socket.socket(socket.AF_INET)
server_sock.bind((host, port)) # ip와 port를 바인딩
server_sock.listen(1)
print("..기다리는 중..") # 안드로이드 앱에서 신호를 주기를 기다림
out_data = "Detect!!" # 연결이 되면 서버에서 안드로이드 앱에 보낼 메시지

try:
    while True: #안드로이드에서 연결 버튼 누를 때까지 기다림
        client_sock, addr = server_sock.accept() # 연결 승인
        if client_sock: # 연결 승인 되었다면
            print('Connected by?!', addr) #연결주소 print
            in_data = client_sock.recv(1024) #안드로이드에서 'refresh" 전송
            print('rcv : ', in_data.decode("utf-8"), len(in_data)) #전송받은 값 디코딩
            
            while in_data: #2초마다 안드로이드에 값을 전달함 (추후 stop, connection 옵션 설정 가능)
                
                if GPIO.input(18) == True: # GPIO의 18핀에 신호가 들어오면 메시지 전송 및 LED에 빛이 들어옴.
                    client_sock.send(str(out_data).encode("utf-8")) #int 값을 string으로 인코딩해서 전송, byte로 전송하면 복잡함
                    print("send: ", out_data)
                    GPIO.output(21,True)
                    sleep(0.3)
                if GPIO.input(18) == False: # GPIO의 18핀에 신호가 들어오지 않으면 
                    GPIO.output(21, False) # LED에 빛이 들어오지 않음
        client_sock.close() #클라이언트 종료
        server_sock.close() #서버 종료
        
except KeyboardInterrupt: #Interrupt 신호가 들어올 시 GPIO 핀 초기화
    GPIO.cleanup()