#include <iostream>

using namespace std;


int main(void)
{

int v;
cin>>v;

for(int i=1;i<=v;i++)
{
for(int j=1;j<=v;j++)
{
int val;
cin>>val;
if(val>-1)
        cout<<i<<" "<<j<<endl;
}
}


}
