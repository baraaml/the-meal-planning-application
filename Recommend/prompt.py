#!/usr/local/pyenv/shims/python3
import os
import sys
import argparse

EXCLUDED_DIRS = {'.git', 'node_modules', 'assets', 'venv', 'dataset2db'}
EXCLUDED_FILES = {'prompt.py', 'index.html', 'meals.csv', '__init__.py' , 'chat.txt'}
EXCLUDED_EXTENSIONS = {'.svg', '.js', '.csv', '.json', '.pyc'}

def should_exclude(entry_name, entry_path):
    if 'dataset2db' in entry_path.split(os.sep):
        return True
    if entry_name in EXCLUDED_FILES:
        return True
    if entry_name.endswith('.pyc'):
        return True
    if os.path.isdir(entry_path) and entry_name in EXCLUDED_DIRS:
        return True
    if os.path.isfile(entry_path) and any(entry_name.endswith(ext) for ext in EXCLUDED_EXTENSIONS):
        return True
    return False

def ask_include(entry_path):
    try:
        resp = input(f"Include file '{entry_path}'? [Y/n]: ").strip().lower()
        return resp in ('', 'y', 'yes')
    except EOFError:
        return False

def print_tree(directory, prefix='', output_file=None):
    try:
        entries = os.listdir(directory)
        entries.sort()
        entries = [e for e in entries if not should_exclude(e, os.path.join(directory, e))]

        for i, entry in enumerate(entries):
            entry_path = os.path.join(directory, entry)
            is_last = (i == len(entries) - 1)
            connector = "└── " if is_last else "├── "

            line = f"{prefix}{connector}{entry}"
            if output_file:
                output_file.write(line + "\n")
            else:
                print(line)

            if os.path.isdir(entry_path):
                new_prefix = prefix + ("    " if is_last else "│   ")
                print_tree(entry_path, new_prefix, output_file)
            else:
                if ask_include(entry_path):
                    try:
                        with open(entry_path, 'r', encoding='utf-8') as file:
                            content = file.read()
                            separator = "//" * 40
                            if output_file:
                                output_file.write(f"\n{separator}\n")
                                output_file.write(content)
                                output_file.write(f"\n{separator}\n\n")
                            else:
                                print(f"\n{separator}")
                                print(content)
                                print(f"{separator}\n")
                    except Exception as e:
                        msg = f"Could not read file '{entry_path}': {e}"
                        if output_file:
                            output_file.write(msg + "\n")
                        else:
                            print(msg)
    except Exception as e:
        msg = f"An error occurred while accessing '{directory}': {e}"
        if output_file:
            output_file.write(msg + "\n")
        else:
            print(msg)

def main():
    parser = argparse.ArgumentParser(description='Print directory tree and optionally include file contents.')
    parser.add_argument('input_dir', help='Input directory path')
    parser.add_argument('-o', '--output', help='Output file path', default='chat.txt')
    args = parser.parse_args()

    if not os.path.isdir(args.input_dir):
        print("Error: The specified input path is not a valid directory.")
        sys.exit(1)

    output_path = args.output
    try:
        with open(output_path, 'w', encoding='utf-8') as output_file:
            output_file.write(f"Directory Tree: {args.input_dir}\n")
            output_file.write("=" * 80 + "\n\n")
            print_tree(args.input_dir, output_file=output_file)
        print(f"\nOutput written to {output_path}")
    except Exception as e:
        print(f"Error writing to output file: {e}")

if __name__ == "__main__":
    main()
