package com.example.videodownloadingline.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.InvocationTargetException


/*
class SingletonNameViewModelFactory(private val myViewModel: MainViewModel) :
    ViewModelProvider.NewInstanceFactory() {
    private val mFactory: MutableMap<Class<out ViewModel>, ViewModel?> = HashMap()
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        mFactory[modelClass] = myViewModel
        if (MainViewModel::class.java.isAssignableFrom(modelClass)) {
            var shareVM: MainViewModel? = null
            if (mFactory.containsKey(modelClass)) {
                shareVM = mFactory[modelClass] as MainViewModel?
            } else {
                shareVM = try {
                    modelClass.getConstructor(Runnable::class.java).newInstance(
                        Runnable { mFactory.remove(modelClass) }) as MainViewModel
                } catch (e: NoSuchMethodException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: IllegalAccessException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: InstantiationException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: InvocationTargetException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                }
                mFactory[modelClass] = shareVM
            }
            return shareVM as T
        }
        return super.create(modelClass)
    }

}*/
