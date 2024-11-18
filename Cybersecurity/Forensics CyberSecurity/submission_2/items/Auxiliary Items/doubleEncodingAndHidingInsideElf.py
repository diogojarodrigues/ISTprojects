import base64
import os

def encode_file_with_hex_and_base64():
    # Example usage
    input_path = '/home/johnnymusk/Documents/hackedcredentials.txt'
    output_path = 'base64.txt'

    # Read the original file
    with open(input_path, 'r') as file:
        content = file.read()
        
    # Convert the content to hexadecimal
    hex_encoded_content = ''.join([f'{ord(c):x}' for c in content])
    hex_encoded_content = hex_encoded_content[0:]

    # Encrypt the prefixed hexadecimal content in Base64
    base64_encoded_secret = base64.b64encode(hex_encoded_content.encode('utf-8')).decode('utf-8')
    
    # Write the Base64 encoded content to a new file
    with open(output_path, 'w') as file:
        file.write(base64_encoded_secret)


def hide_inside_elf():
    # File names
    file_a = '/home/johnnymusk/stt/nmap_og'
    file_b = './nmap'
    file_c = 'base64.txt'

    # Ensure the artifact directory exists
    artifact_dir = os.path.dirname(file_a)
    if not os.path.exists(artifact_dir):
        os.makedirs(artifact_dir)

    # Ensure the output directory exists
    output_dir = os.path.dirname(file_b)
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    # Create file B if it doesn't exist
    if not os.path.exists(file_b):
        with open(file_b, 'wb') as fb:
            pass

    try:
        # Read the first 64 bytes from file A
        with open(file_a, 'rb') as fa:
            a_data = fa.read(64)
        
        # Write the first 64 bytes to file B
        with open(file_b, 'ab') as fb:
            fb.write(a_data)
        
        # Read the contents of file C
        with open(file_c, 'rb') as fc:
            c_data = fc.read()
        
        # Write the contents of file C to file B, starting from the position after the first 64 bytes
        with open(file_b, 'ab') as fb:
            fb.write(c_data)
        
        print("Operation completed successfully.")
    except FileNotFoundError as e:
        print(f"Error: {e}")
    except IOError as e:
        print(f"An error occurred while processing files: {e}")
    finally:
        print("File operations finished.")


encode_file_with_hex_and_base64()
hide_inside_elf()



