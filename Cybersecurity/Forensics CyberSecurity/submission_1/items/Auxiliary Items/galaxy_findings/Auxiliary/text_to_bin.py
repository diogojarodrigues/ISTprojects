with open('concat.txt', 'r') as file:
    binary_data = file.read().strip()  # Read the file and remove any extra spaces or newlines

    byte_chunks = [binary_data[i:i+8] for i in range(0, len(binary_data), 8)]

    text_output = ''.join([chr(byte) for byte in byte_chunks])

    with open('output.txt', 'w') as output_file:
        output_file.write(text_output)
