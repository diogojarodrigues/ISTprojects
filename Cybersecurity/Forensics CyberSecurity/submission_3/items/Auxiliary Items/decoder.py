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

def add_base64_padding(encoded_string):
    # Add padding to make the Base64 string length a multiple of 4
    return encoded_string + '=' * (-len(encoded_string) % 4)

def decrypt(enc, password):
    private_key = hashlib.sha256(password.encode("utf-8")).digest()
    enc = base64.b64decode(enc)  #Only if your content is base64 encoded
    #enc = base64.b64decode(add_base64_padding(enc))
    iv = enc[:16]  # Extract the IV
    cipher = AES.new(private_key, AES.MODE_CFB, iv)
    return cipher.decrypt(enc[16:])  # Decrypt the rest

filename = sys.argv[1]

try:
    with open(filename, 'r') as file:  # Open the file in binary mode
        message = file.read()
        if message[:3] == "cmd" :
          message = urllib.parse.parse_qs(message)['cmd'][0]
        else :
          message = urllib.parse.parse_qs(message)['file'][0]
        message = str(decrypt(message, password), 'utf-8')
        print(message)

except Exception as e:
    print(f"An error occurred: {e}")
