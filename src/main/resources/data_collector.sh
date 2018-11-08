#!/bin/bash
function os_info(){

  cat /etc/*-release | grep PRETTY_NAME

}

function memory_info(){

  free | grep 'Mem:' |  awk '{print  $2, $3, $4, $6}'

}

function free_disk(){

  df | grep -v '1K' | awk '{print $4}'

}

function used_disk(){

  df | grep -v '1K' | awk '{print $3}'

}

if [ -z $1 ]; then

  echo "error"
  exit

elif [ $1 == 1 ]; then

  os_info

elif [ $1 == 2 ]; then

  memory_info

elif [ $1 == 3 ]; then

  free_disk

elif [ $1 == 4 ]; then

  used_disk

else
  echo "error"
  exit
fi
