import sys

files = ['text-ui-test/EXPECTED.TXT', 'text-ui-test/input.txt', 'text-ui-test/runtest.sh']

for f in files:
    try:
        with open(f, 'rb') as fp:
            data = fp.read()
        
        if data.startswith(b'\xef\xbb\xbf'):
            data = data[3:]
        elif data.startswith(b'\xff\xfe'):
            data = data[2:]
        elif data.startswith(b'\xfe\xff'):
            data = data[2:]
            
        if b'\x00' in data:
            text = data.decode('utf-16le', errors='ignore')
        else:
            text = data.decode('utf-8', errors='ignore')
            
        text = text.replace('\r\n', '\n')
        
        with open(f, 'wb') as fp:
            fp.write(text.encode('utf-8'))
            
        print(f"Successfully processed {f}")
    except Exception as e:
        print(f"Error processing {f}: {e}")
